package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.util.FileUtils;
import com.linksteady.operate.dao.QywxDailyCouponMapper;
import com.linksteady.operate.dao.QywxDailyDetailMapper;
import com.linksteady.operate.dao.QywxSendCouponMapper;
import com.linksteady.operate.domain.QywxDailyDetail;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.QywxDailyDetailService;
import com.linksteady.operate.service.QywxMdiaService;
import com.linksteady.operate.service.ShortUrlService;
import com.linksteady.operate.thread.TransQywxDailyContentThread;
import com.linksteady.operate.vo.FollowUserVO;
import com.linksteady.operate.vo.GroupCouponVO;
import com.linksteady.operate.vo.RecProdVo;
import com.linksteady.smp.starter.lognotice.service.ExceptionNoticeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 日运营头表
 *
 * @author hxcao
 * @date 2019-07-31
 */
@Service
@Slf4j
public class QywxDailyDetailServiceImpl implements QywxDailyDetailService {

    @Autowired
    private QywxDailyDetailMapper qywxDailyDetailMapper;

    @Autowired
    private QywxDailyCouponMapper couponMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    ConfigService configService;

    @Autowired
    ShortUrlService shortUrlService;

    @Autowired
    QywxMdiaService qywxMdiaService;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @Autowired
    QywxSendCouponMapper qywxSendCouponMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generate(Long headerId) throws Exception {
        qywxDailyDetailMapper.deleteQywxPushContentTemp(headerId);
        //获取group上配置的所有补贴信息
        List<Map<String, Object>> groupCouponInfo = couponMapper.selectGroupCouponInfo();
        Map<String, List<GroupCouponVO>> groupCouponList = groupingCouponByGroupId(groupCouponInfo);
        Long startTime = System.currentTimeMillis();

        //本次所有商品的mediaId
        Map<String,String> mediaMap= Maps.newHashMap();
        List<String> productIdList=qywxDailyDetailMapper.getProductIdList(headerId);
        for(int i=0;i<productIdList.size();i++)
        {
            mediaMap.put(productIdList.get(i),qywxMdiaService.getMpMediaId(productIdList.get(i)));
        }
        //根据headerID获取当前有多少人需要推送
        int pushUserCount = qywxDailyDetailMapper.getUserCount(headerId);
        int pageSize = 200;
        //判断如果条数大于200 则进行分页
        if (pushUserCount <= pageSize) {
            List<QywxDailyDetail> list = qywxDailyDetailMapper.getUserList(headerId, pushUserCount, 0);
            //填充模板 生成文案
            List<QywxDailyDetail> targetList = transContent(list, groupCouponList,mediaMap);
            //保存要推送的文案
            if (null != targetList && targetList.size() > 0) {
                qywxDailyDetailMapper.insertPushContentTemp(targetList);
            }
        } else {
            ExecutorService pool = null;
            try {
                ThreadFactory qywxDailyThreadFactory = new ThreadFactoryBuilder()
                        .setNameFormat("qywx-daily-content-trans-pool-%d").build();

                //生成线程池 (线程数量为4)
                pool = new ThreadPoolExecutor(8,
                        8,
                        1000,
                        TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>(), qywxDailyThreadFactory);

                //分页多线程处理
                int page = pushUserCount % pageSize == 0 ? pushUserCount / pageSize : (pushUserCount / pageSize + 1);

                List taskList = Lists.newArrayList();
                //生成线程对象列表
                for (int i = 0; i < page; i++) {
                    taskList.add(new TransQywxDailyContentThread(headerId, pageSize, i * pageSize, groupCouponList,mediaMap));
                }

                log.info("转换文案一共需要{}个线程来处理", taskList.size());
                //放入线程池中
                List<Future<List<QywxDailyDetail>>> threadResult = pool.invokeAll(taskList);
                for (Future<List<QywxDailyDetail>> future : threadResult) {
                    if (null != future.get() && future.get().size() > 0) {
                        qywxDailyDetailMapper.insertPushContentTemp(future.get());
                    } else {
                        throw new LinkSteadyException("转化文案失败");
                    }
                }
            } catch (Exception e) {
                //错误日志上报
                log.error("每日运营[企业微信]转化文案错误，错误堆栈为{}", e);

                //上报
                exceptionNoticeHandler.exceptionNotice(e);

                //异常向上抛出
                throw e;
            } finally {
                pool.shutdown();
            }
        }

        //用临时表更新 每日运营明细表
        qywxDailyDetailMapper.updatePushContentFromTemp(headerId);
        Long endTime = System.currentTimeMillis();
        log.info("每日运营[企业微信]文案已生成，共：{}人，耗时：{}", pushUserCount, endTime - startTime);
    }

    @Override
    public List<QywxDailyDetail> getQywxDetailList(Long headId, int limit, int offset, String followUserId,long recProdId) {
        return qywxDailyDetailMapper.getQywxDetailList(headId, limit, offset, followUserId,recProdId);
    }

    @Override
    public int getQywxDetailCount(Long headId, String followUserId,long recProdId) {
        return qywxDailyDetailMapper.getQywxDetailCount(headId, followUserId,recProdId);
    }

    @Override
    public List<QywxDailyDetail> getConversionList(Long headId, int limit, int offset, String followUserId) {
        return qywxDailyDetailMapper.getConversionList(headId, limit, offset, followUserId);
    }

    @Override
    public int getConversionCount(Long headId, String followUserId) {
        return qywxDailyDetailMapper.getConversionCount(headId, followUserId);
    }

    @Override
    public List<FollowUserVO> getAllFollowUserList(Long headId) {
        return qywxDailyDetailMapper.getAllFollowUserList(headId);
    }

    @Override
    public List<RecProdVo> getRecProdList(Long headId) {
        return qywxDailyDetailMapper.getRecProdList(headId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delDetail(Long headId, List<Long> list) {
        qywxDailyDetailMapper.resetPushDel(headId,list);
        //更新头表的数量用户数量
        qywxDailyDetailMapper.updateTotalNum(list.size(),headId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadCoupon(MultipartFile file, Long couponId,String couponIdentity)throws LinkSteadyException {
        // 解析file
        List<String> mobiles = Lists.newArrayList();
        String xlsSuffix = ".xls";
        String xlsxSuffix = ".xlsx";
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!(fileType.equalsIgnoreCase(xlsSuffix) || fileType.equalsIgnoreCase(xlsxSuffix))) {
            throw new LinkSteadyException("文件格式不符，只支持.xls,.xlsx后缀的文件！");
        }
        Workbook workbook = null;
        try {
            InputStream is = file.getInputStream();
            if (fileType.equalsIgnoreCase(xlsSuffix)) {
                workbook = new HSSFWorkbook(is);
            } else if (fileType.equalsIgnoreCase(xlsxSuffix)) {
                workbook = new XSSFWorkbook(is);
            }
            Sheet sheet = workbook.getSheetAt(0);
            if (null == sheet) {
                throw new LinkSteadyException("系统只解析第一个sheet，当前文件第一个sheet为空");
            } else{
                for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    Cell cell = row.getCell(0);
                    if(StringUtils.isNotEmpty(cell.getStringCellValue())){
                        mobiles.add(cell.getStringCellValue());
                    }
                }
                FileUtils.deleteTempFile(FileUtils.multipartFileToFile(file));
            }
        }catch (IOException e){
            throw new LinkSteadyException("文件解析异常！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //获取数据，更新到uo_coupon_serial_no中
        qywxSendCouponMapper.uploadCoupon(mobiles,couponId,couponIdentity);
    }

    @Override
    public void couponToSequence(Long couponId,String couponIdentity) throws LinkSteadyException{
        //获取当前的流水号
        int couponSn=qywxSendCouponMapper.getCouponSn();
        if(couponSn==0) {
            couponSn=1;
        }
        //最后一个序号
        int lastCouponSn=couponSn+100;

        //更新最后一个序号到数据库
        qywxSendCouponMapper.updateCouponSn(lastCouponSn);
        List<String> couponSnList= Lists.newArrayList();
        //遍历生成序号
        for(int i=couponSn+1;i<=lastCouponSn;i++){
            couponSnList.add(String.format("%012d", i));
        }
        //获取数据，更新到uo_coupon_serial_no中
        qywxSendCouponMapper.uploadCoupon(couponSnList,couponId, couponIdentity);
    }

    /**
     * 对配在组上的补贴按照GROUP_ID进行分组
     *
     * @param groupCouponInfo
     * @return
     */
    private Map<String, List<GroupCouponVO>> groupingCouponByGroupId(List<Map<String, Object>> groupCouponInfo) {
        List<GroupCouponVO> list = groupCouponInfo.stream().map(p -> {
            return new GroupCouponVO(
                    p.get("group_id").toString(),
                    p.get("coupon_id").toString(),
                    (String) p.get("coupon_display_name"),
                    Double.parseDouble(p.get("coupon_denom").toString()),
                    Double.parseDouble(p.get("coupon_threshold").toString()),
                    (String) p.get("coupon_url")
            );
        }).collect(Collectors.toList());

        Map<String, List<GroupCouponVO>> result = list.stream().collect(Collectors.groupingBy(GroupCouponVO::getGroupId));
        return result;
    }

    /**
     * 根据查询到的用户及其推荐信息，结合模板，生成用户的最终推送信息列表
     *
     * @param list
     * @return
     */
    public List<QywxDailyDetail> transContent(List<QywxDailyDetail> list, Map<String, List<GroupCouponVO>> groupCouponList,Map<String,String> mediaMap) {
        List<QywxDailyDetail> targetList = Lists.newArrayList();
        QywxDailyDetail temp = null;

        for (QywxDailyDetail qywxDailyDetail : list) {
            temp = new QywxDailyDetail();

            //文案内容
            String textContent = qywxDailyDetail.getTextContent();
            textContent = textContent.replace("${商品名称}", convertNullToEmpty(qywxDailyDetail.getRecProdName()));

            //判断当前组是否含券 1表示含券，匹配补贴信息
            if (1 == qywxDailyDetail.getIsCoupon()) {
                //匹配补贴信息
                //获取推荐件单价 Rec_Piece_Price
                double recpiecePrice = qywxDailyDetail.getPiecePrice();

                //最佳补贴的对象
                GroupCouponVO couponTemp = null;
                //推荐件单价和补贴门槛之间的差额  (差额最小的那个补贴就是推荐的补贴)
                double distanceTemp = 0d;
                //门槛最小的补贴
                GroupCouponVO minCoupon = null;
                int count = 0;


                //根据当前的group_id获取当前组上配的补贴列表
                List<GroupCouponVO> couponList = groupCouponList.get(qywxDailyDetail.getGroupId());
                if (null != couponList && couponList.size() > 0) {
                    for (GroupCouponVO groupCouponVO : couponList) {
                        //计算推荐单价和补贴门槛的差额 (取低于推荐单价 且 最接近最低单价的门槛)
                        double diff = recpiecePrice - groupCouponVO.getCouponThreshold();
                        if (diff > 0) {
                            //首次
                            if (count == 0) {
                                distanceTemp = diff;
                                couponTemp = groupCouponVO;
                                count += 1;
                            } else {
                                //以后每次 如果获取到最接近的，则更新
                                if (diff < distanceTemp) {
                                    distanceTemp = diff;
                                    couponTemp = groupCouponVO;
                                }
                            }

                        }

                        if (minCoupon == null) {
                            minCoupon = groupCouponVO;
                        } else {
                            if (groupCouponVO.getCouponThreshold() < minCoupon.getCouponThreshold()) {
                                //替换门槛最小的补贴为当前补贴
                                minCoupon = groupCouponVO;
                            }
                        }
                    }
                }

                //如果找低于推荐价格且最接近的补贴 找不到，则取门槛最小的那个补贴
                if (couponTemp == null) {
                    couponTemp = minCoupon;
                }
                if (null != couponTemp) {
                    textContent = textContent.replace("${补贴短链}", convertNullToEmpty(couponTemp.getCouponUrl()));
                    textContent = textContent.replace("${补贴名称}", convertNullToEmpty(couponTemp.getCouponDisplayName()));

                    temp.setCouponId(couponTemp.getCouponId());
                    temp.setCouponDeno(couponTemp.getCouponDenom());
                    //补贴门槛
                    temp.setCouponMin(String.valueOf(couponTemp.getCouponThreshold()));
                } else {
                    temp.setCouponId("-1");
                }

            } else {
                temp.setCouponId("-1");
            }

            temp.setDetailId(qywxDailyDetail.getDetailId());
            temp.setTextContent(textContent);
            temp.setHeadId(qywxDailyDetail.getHeadId());
            temp.setMpMediaId(mediaMap.get(qywxDailyDetail.getRecProdId()));

            //构造qywxMsgSign  textcontent、mptitle、mpurl三者联合做签名
            temp.setQywxMsgSign(new Md5Hash(textContent+"|"+qywxDailyDetail.getMpTitle()+"|"+qywxDailyDetail.getMpUrl()).toString());
            targetList.add(temp);
        }
        return targetList;
    }


    /**
     * 如果传入的字符串为null，则返回空字符串
     *
     * @param obj
     * @return
     */
    private String convertNullToEmpty(String obj) {
        if (null == obj) {
            return "";
        } else {
            return obj;
        }

    }

    /**
     * 分页获取选中的用户名单
     *
     * @param headerId
     * @param limit
     * @param offset
     * @return
     */
    public List<QywxDailyDetail> getUserList(Long headerId, int limit, int offset) {
        return qywxDailyDetailMapper.getUserList(headerId, limit, offset);
    }

}
