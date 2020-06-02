package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.dao.CouponMapper;
import com.linksteady.operate.dao.QywxDailyDetailMapper;
import com.linksteady.operate.domain.QywxDailyDetail;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.QywxDailyDetailService;
import com.linksteady.operate.service.ShortUrlService;
import com.linksteady.operate.thread.TransQywxDailyContentThread;
import com.linksteady.operate.vo.GroupCouponVO;
import com.linksteady.operate.vo.QywxUserVO;
import com.linksteady.smp.starter.lognotice.service.ExceptionNoticeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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
    private CouponMapper couponMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    ConfigService configService;

    @Autowired
    ShortUrlService shortUrlService;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generate(Long headerId) throws Exception {
        qywxDailyDetailMapper.deleteQywxPushContentTemp(headerId);
        //获取group上配置的所有优惠券信息
        List<Map<String,Object>> groupCouponInfo=couponMapper.selectGroupCouponInfo();
        Map<String,List<GroupCouponVO>> groupCouponList=groupingCouponByGroupId(groupCouponInfo);
        Long startTime = System.currentTimeMillis();

        //根据headerID获取当前有多少人需要推送
        int pushUserCount= qywxDailyDetailMapper.getUserCount(headerId);
        int pageSize=200;
        //判断如果条数大于200 则进行分页
        if(pushUserCount<=pageSize)
        {
            List<QywxDailyDetail> list = qywxDailyDetailMapper.getUserList(headerId,pushUserCount, 0);
            //填充模板 生成文案
            List<QywxDailyDetail> targetList=transContent(list,groupCouponList);
            //保存要推送的文案
            if(null!=targetList&&targetList.size()>0)
            {
                qywxDailyDetailMapper.insertPushContentTemp(targetList);
            }
        }else
        {
            ExecutorService pool = null;
            try {
                ThreadFactory qywxDailyThreadFactory = new ThreadFactoryBuilder()
                        .setNameFormat("qywx-daily-content-trans-pool-%d").build();

                //生成线程池 (线程数量为4)
                pool = new ThreadPoolExecutor(8,
                        8,
                        1000,
                        TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>(),qywxDailyThreadFactory);

                //分页多线程处理
                int page=pushUserCount%pageSize==0?pushUserCount/pageSize:(pushUserCount/pageSize+1);

                List taskList= Lists.newArrayList();
                //生成线程对象列表
                for(int i=0;i<page;i++)
                {
                    taskList.add(new TransQywxDailyContentThread(headerId,i*pageSize,pageSize,groupCouponList));
                }

                log.info("转换文案一共需要{}个线程来处理",taskList.size());
                //放入线程池中
                List<Future<List<QywxDailyDetail>>> threadResult=pool.invokeAll(taskList);
                for(Future<List<QywxDailyDetail>> future:threadResult)
                {
                    if(null!=future.get()&&future.get().size()>0)
                    {
                        qywxDailyDetailMapper.insertPushContentTemp(future.get());
                    }else
                    {
                        throw new LinkSteadyException("转化文案失败");
                    }
                }
            } catch (Exception e) {
                //错误日志上报
                log.error("每日运营[企业微信]转化文案错误，错误堆栈为{}",e);

                //上报
                exceptionNoticeHandler.exceptionNotice(e);

                //异常向上抛出
                throw e;
            }finally {
                pool.shutdown();
            }
        }

        //用临时表更新 每日运营明细表
        qywxDailyDetailMapper.updatePushContentFromTemp(headerId);
        Long endTime = System.currentTimeMillis();
        log.info("每日运营[企业微信]文案已生成，共：{}人，耗时：{}", pushUserCount, endTime - startTime);
    }

    @Override
    public List<QywxDailyDetail> getQywxDetailList(Long headId, int limit, int offset, String qywxUserId) {
        return qywxDailyDetailMapper.getQywxDetailList(headId,limit,offset,qywxUserId);
    }

    @Override
    public int getQywxDetailCount(Long headId, String qywxUserId) {
        return qywxDailyDetailMapper.getQywxDetailCount(headId,qywxUserId);
    }

    @Override
    public List<QywxDailyDetail> getConversionList(Long headId, int limit, int offset, String qywxUserId) {
        return qywxDailyDetailMapper.getConversionList(headId,limit,offset,qywxUserId);
    }

    @Override
    public int getConversionCount(Long headId, String qywxUserId) {
        return qywxDailyDetailMapper.getConversionCount(headId,qywxUserId);
    }

    @Override
    public List<QywxUserVO> getQywxUserList(Long headId) {
        return qywxDailyDetailMapper.getQywxUserList(headId);
    }

    @Override
    public List<Map<String,String>> getTestPushData() {
        List<Map<String,String>> pushResult=qywxDailyDetailMapper.getTestPushData();
        return pushResult;
    }

    /**
     * 对配在组上的优惠券按照GROUP_ID进行分组
     * @param groupCouponInfo
     * @return
     */
    private   Map<String,List<GroupCouponVO>> groupingCouponByGroupId(List<Map<String,Object>> groupCouponInfo)
    {
        List<GroupCouponVO> list=groupCouponInfo.stream().map(p->{
            return new GroupCouponVO(
                    p.get("group_id").toString(),
                    p.get("coupon_id").toString(),
                    (String)p.get("coupon_display_name"),
                    Double.parseDouble(p.get("coupon_denom").toString()),
                    Double.parseDouble(p.get("coupon_threshold").toString()),
                    (String)p.get("coupon_url")
            );
        }).collect(Collectors.toList());

        Map<String,List<GroupCouponVO>> result=list.stream().collect(Collectors.groupingBy(GroupCouponVO::getGroupId));
        return result;
    }

    /**
     * 根据查询到的用户及其推荐信息，结合模板，生成用户的最终推送信息列表
     * @param list
     * @return
     */
    public List<QywxDailyDetail> transContent(List<QywxDailyDetail> list,Map<String,List<GroupCouponVO>> groupCouponList) {
        List<QywxDailyDetail> targetList = Lists.newArrayList();
        QywxDailyDetail temp = null;

        for (QywxDailyDetail qywxDailyDetail : list) {
            temp = new QywxDailyDetail();

            //文案内容
            String textContent = qywxDailyDetail.getTextContent();
            textContent = textContent.replace("${PROD_NAME}", convertNullToEmpty(qywxDailyDetail.getRecProdName()));

            //判断当前组是否含券 1表示含券，匹配优惠券信息
            if(1==qywxDailyDetail.getIsCoupon())
            {
                //匹配优惠券信息
                //获取推荐件单价 Rec_Piece_Price
                double recpiecePrice=qywxDailyDetail.getPiecePrice();

                //最佳优惠券的对象
                GroupCouponVO couponTemp=null;
                //推荐件单价和优惠券门槛之间的差额  (差额最小的那个优惠券就是推荐的优惠券)
                double distanceTemp=0d;
                //门槛最小的优惠券
                GroupCouponVO minCoupon=null;
                int count=0;


                //根据当前的group_id获取当前组上配的优惠券列表
                List<GroupCouponVO> couponList=groupCouponList.get(qywxDailyDetail.getGroupId());
                if(null!=couponList&&couponList.size()>0)
                {
                    for(GroupCouponVO groupCouponVO:couponList)
                    {
                        //计算推荐单价和优惠券门槛的差额 (取低于推荐单价 且 最接近最低单价的门槛)
                        double diff=recpiecePrice-groupCouponVO.getCouponThreshold();
                        if(diff>0)
                        {
                            //首次
                            if(count==0)
                            {
                                distanceTemp=diff;
                                couponTemp=groupCouponVO;
                                count+=1;
                            }else
                            {
                                //以后每次 如果获取到最接近的，则更新
                                if(diff<distanceTemp)
                                {
                                    distanceTemp=diff;
                                    couponTemp=groupCouponVO;
                                }
                            }

                        }

                        if(minCoupon==null)
                        {
                            minCoupon=groupCouponVO;
                        }else
                        {
                            if(groupCouponVO.getCouponThreshold()<minCoupon.getCouponThreshold())
                            {
                                //替换门槛最小的优惠券为当前优惠券
                                minCoupon=groupCouponVO;
                            }
                        }
                    }
                }

                //如果找低于推荐价格且最接近的优惠券 找不到，则取门槛最小的那个优惠券
                if(couponTemp==null)
                {
                    couponTemp=minCoupon;
                }
                if(null!=couponTemp)
                {
                    textContent = textContent.replace("${COUPON_URL}", convertNullToEmpty(couponTemp.getCouponUrl()));
                    textContent = textContent.replace("${COUPON_NAME}", convertNullToEmpty(couponTemp.getCouponDisplayName()));

                    temp.setCouponId(couponTemp.getCouponId());
                    temp.setCouponDeno(String.valueOf(couponTemp.getCouponDenom()));
                    //优惠券门槛
                    temp.setCouponMin(String.valueOf(couponTemp.getCouponThreshold()));
                }else
                {
                    temp.setCouponId("-1");
                }

            }else
            {
                temp.setCouponId("-1");
            }

            //判断是否含有产品详情页链接
            if(null!=textContent&&textContent.indexOf("${PROD_URL}")!=-1)
            {
                //获取商品的短链
                String prodLongUrl=shortUrlService.genProdShortUrlByProdId(qywxDailyDetail.getRecProdId(),"S");
                //如果短链生成错误，则不再进行替换
                if(!"error".equals(prodLongUrl))
                {
                    textContent = textContent.replace("${PROD_URL}",prodLongUrl);
                }
            }

            temp.setDetailId(qywxDailyDetail.getDetailId());
            temp.setTextContent(textContent);
            temp.setHeadId(qywxDailyDetail.getHeadId());

            targetList.add(temp);
        }
        return targetList;
    }


    /**
     * 如果传入的字符串为null，则返回空字符串
     * @param obj
     * @return
     */
    private String convertNullToEmpty(String obj)
    {
        if(null==obj)
        {
            return "";
        }else
        {
            return obj;
        }

    }

    /**
     * 分页获取选中的用户名单
     * @param headerId
     * @param limit
     * @param offset
     * @return
     */
    public List<QywxDailyDetail>  getUserList(Long headerId,int limit,int offset){
        return qywxDailyDetailMapper.getUserList(headerId,limit,offset);
    }

}