package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QywxMessage;
import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.dao.QywxDailyDetailMapper;
import com.linksteady.operate.dao.QywxDailyMapper;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.exception.OptimisticLockException;
import com.linksteady.operate.exception.SendCouponException;
import com.linksteady.operate.service.QywxDailyService;
import com.linksteady.operate.service.QywxMessageService;
import com.linksteady.operate.service.QywxSendCouponService;
import com.linksteady.operate.vo.FollowUserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 日运营头表
 *
 * @author hxcao
 * @date 2019-07-31
 */
@Slf4j
@Service
public class QywxDailyServiceImpl implements QywxDailyService {

    @Autowired
    private QywxDailyMapper qywxDailyMapper;

    @Autowired
    private QywxDailyDetailMapper qywxDailyDetailMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    ConfigService configService;

    @Autowired
    private QywxMessageService qywxMessageService;

    @Autowired
    QywxSendCouponService qywxSendCouponService;

    @Override
    public List<QywxDailyHeader> getHeadList(int limit, int offset, String taskDate) {
        return qywxDailyMapper.getHeadList(limit, offset, taskDate);
    }

    @Override
    public int getTotalCount(String touchDt) {
        return qywxDailyMapper.getTotalCount(touchDt);
    }


    @Override
    public boolean getTransContentLock(String headId) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //如果key不存在，则将key的值设置为value，同时返回true. 如果key不存在，则什么也不做，返回false.
        boolean flag = valueOperations.setIfAbsent("qywx_daily_trans_lock", headId, 60, TimeUnit.SECONDS);
        return flag;
    }

    @Override
    public void delTransLock() {
        redisTemplate.delete("qywx_daily_trans_lock");
    }

    @Override
    public QywxDailyHeader getHeadInfo(Long headId) {
        return qywxDailyMapper.getHeadInfo(headId);
    }

    /**
     * 对每日运营进行推送
     *
     * @param qywxDailyHeader 头信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void push(QywxDailyHeader qywxDailyHeader, Long effectDays) throws Exception {
        //更新状态为已执行
        int count = qywxDailyMapper.updateStatus(qywxDailyHeader.getHeadId(), "done", effectDays,qywxDailyHeader.getVersion());
        if (count == 0) {
            throw new OptimisticLockException("记录已被其他用户修改，请返回刷新后重试");
        }
        Long headId = qywxDailyHeader.getHeadId();
        //判断优惠券是否已发放 如果未发放，则进行优惠券的发放
        if ("N".equals(qywxDailyHeader.getCouponSendFlag())) {
            try {
                boolean flag = qywxSendCouponService.sendCouponToUser(headId);
                if (!flag) {
                    throw new SendCouponException("券已发出，但接口返回的结果为异常");
                }else
                {
                    qywxDailyMapper.updateSendCouponFlag(headId);
                }
            } catch (Exception e) {
                throw new SendCouponException("发送优惠券异常");
            }
        }

        String appId = qywxMessageService.getMpAppId();
        //按导购分组
        List<FollowUserVO> followUserIdList =qywxDailyDetailMapper.getFollowUserList(headId);

        followUserIdList.forEach(x -> {
            String followUserId = x.getFollowUserId();
            // 推送消息(按消息分组)
            List<String> msgSignList =qywxDailyDetailMapper.getMessageSignList(headId,followUserId);

            //备注：同一msgSignList下必然是同一个商品
            msgSignList.forEach(y -> {

                //查询当前签名、当前导购下记录的条数
                int waitCount=qywxDailyDetailMapper.getWaitQywxUserListCount(headId,followUserId,y);
                int pageSize = 10000;
                if (waitCount <= pageSize) {
                    log.info("当前推送数据量<=10000");
                    if(waitCount > 0) {
                        //获取当前待推送的列表
                        List<QywxDailyDetail> qywxDailyDetailList=qywxDailyDetailMapper.getQywxUserList(headId,followUserId,y,waitCount,0);

                        QywxPushList qywxPushList=new QywxPushList();
                        qywxPushList.setTextContent(qywxDailyDetailList.get(0).getTextContent());
                        qywxPushList.setMpTitle(qywxDailyDetailList.get(0).getMpTitle());
                        qywxPushList.setMpUrl(qywxDailyDetailList.get(0).getMpUrl());
                        qywxPushList.setMpMediaId(qywxDailyDetailList.get(0).getMpMediaId());
                        qywxPushList.setMpAppid(appId);
                        qywxPushList.setExternalContactIds(StringUtils.join(qywxDailyDetailList.stream().map(QywxDailyDetail::getQywxContractId).collect(Collectors.toList()),","));
                        qywxPushList.setFollowUserId(followUserId);
                        qywxPushList.setSourceId(qywxDailyDetailList.get(0).getHeadId());
                        qywxDailyMapper.insertPushList(qywxPushList);
                        //推送并更新状态
                        try {
                            pushQywxMsg(qywxPushList,qywxDailyDetailList);
                        } catch (Exception e) {
                            new LinkSteadyException("推送任务出现异常！"+e);
                        }
                    }
                } else {
                    int pageNum = waitCount % pageSize == 0 ? (waitCount / pageSize) : ((waitCount / pageSize) + 1);
                    for (int i = 0; i < pageNum; i++) {
                        log.info("当前文本推送条数{}，偏移量为{}", pageSize,i*pageSize);
                        List<QywxDailyDetail> tmpUserList = qywxDailyDetailMapper.getQywxUserList(headId,followUserId,y,pageSize,i * pageSize);
                        if(tmpUserList.size() > 0) {
                            QywxPushList qywxPushList=new QywxPushList();
                            qywxPushList.setTextContent(tmpUserList.get(0).getTextContent());
                            qywxPushList.setMpTitle(tmpUserList.get(0).getMpTitle());
                            qywxPushList.setMpUrl(tmpUserList.get(0).getMpUrl());
                            qywxPushList.setMpMediaId(tmpUserList.get(0).getMpMediaId());
                            qywxPushList.setMpAppid(appId);
                            qywxPushList.setExternalContactIds(StringUtils.join(tmpUserList.stream().map(QywxDailyDetail::getQywxContractId).collect(Collectors.toList()),","));
                            qywxPushList.setFollowUserId(followUserId);
                            qywxPushList.setSourceId(tmpUserList.get(0).getHeadId());
                            qywxDailyMapper.insertPushList(qywxPushList);
                            //推送并更新状态
                            try {
                                pushQywxMsg(qywxPushList,tmpUserList);
                            } catch (Exception e) {
                                new LinkSteadyException("推送任务出现异常！"+e);
                            }
                        }
                    }
                }
            });
        });
    }

    /**
     * 推送企业微信消息
     *
     * @param qywxPushList (待推送的对象)
     */
    private void pushQywxMsg(QywxPushList qywxPushList,List<QywxDailyDetail> qywxDailyDetailList)throws Exception {
        if(null==qywxDailyDetailList||qywxDailyDetailList.size()==0)
        {
            qywxDailyMapper.updatePushList(qywxPushList.getPushId(),"F","","","推送列表为空");
            return;
        }

        String msgContent = qywxPushList.getTextContent();
        String mpTitle = qywxPushList.getMpTitle();
        String mpUrl = qywxPushList.getMpUrl();
        String mediaId =qywxPushList.getMpMediaId();
        String appId = qywxPushList.getMpAppid();

        //判断文本或小程序至少有一个的变量
        boolean flag=true;
        QywxMessage qywxMessage = new QywxMessage();

        if (StringUtils.isNotEmpty(msgContent)) {
            qywxMessage.setText(msgContent);
            flag=false;
        }
        if(StringUtils.isNotEmpty(mpTitle))
        {
            qywxMessage.setMpTitle(mpTitle);
            qywxMessage.setMpPicMediaId(mediaId);
            qywxMessage.setMpAppid(appId);
            qywxMessage.setMpPage(mpUrl);
            flag=false;
        }

        //消息中至少有文本、小程序中的任何一个，才进行推送
        if(flag)
        {
            qywxDailyMapper.updatePushList(qywxPushList.getPushId(),"F","","","消息为空");

        }else
        {
            List<String> contactIdList=qywxDailyDetailList.stream().map(QywxDailyDetail::getQywxContractId).collect(Collectors.toList());
            String result = qywxMessageService.pushQywxMessage(qywxMessage, qywxPushList.getFollowUserId(), contactIdList);
            log.info("日运营企微：推送结果【{}】", result);

            String status="S";
            String msgId ="";
            String failList="";
            String remark="推送成功";

            if(StringUtils.isEmpty(result))
            {
                 status="F";
                remark="调用企业微信接口返回空";
            }else
            {
                JSONObject jsonObject = JSON.parseObject(result);
                msgId = jsonObject.getString("msgid");
                int errcode = jsonObject.getIntValue("errcode");
                failList = jsonObject.getString("fail_list");

                if(errcode!=0)
                {
                    status="F";
                    remark="调用企业微信接口失败";
                }
            }
            qywxDailyMapper.updatePushList(qywxPushList.getPushId(),status,msgId,failList,remark);

            //更新uo_qywx_daily_detail表上的push_id
            List<Long> detailIdList=qywxDailyDetailList.stream().map(QywxDailyDetail::getDetailId).collect(Collectors.toList());
            log.info("更新pushidpushid:{}",qywxPushList.getPushId());
            if(detailIdList.size()>0)
            {
                qywxDailyDetailMapper.updatePushId(detailIdList,qywxPushList.getPushId(),msgId,status);
            }
        }
    }

    @Override
    public Map<String, Object> getPushEffectChange(Long headId) {
        Map<String, Object> result = Maps.newHashMap();
        String dateFormat = "yyyyMMdd";
        List<LocalDate> xdatas = Lists.newLinkedList();

        QywxDailyHeader qywxDailyHeader = qywxDailyMapper.getHeadInfo(headId);
        // 提交任务日期
        String taskDt = qywxDailyHeader.getTaskDateStr();

        //获取任务观察的天数
        long effectDays = qywxDailyHeader.getEffectDays();

        //任务提交的日期
        LocalDate taskDtDate = LocalDate.parse(taskDt, DateTimeFormatter.ofPattern(dateFormat));

        // 任务期最后时间
        LocalDate maxDate = taskDtDate.plusDays(effectDays + 1);

        //时间轴的数据
        while (taskDtDate.isBefore(maxDate)) {
            xdatas.add(taskDtDate);
            taskDtDate = taskDtDate.plusDays(1);
        }

        List<QywxDailyStatis> dataList = qywxDailyMapper.getQywxDailyStatisList(headId);
        Map<String, QywxDailyStatis> dailyStatisMap = dataList.stream().collect(Collectors.toMap(QywxDailyStatis::getConversionDateStr, a -> a));

        //转化人数
        List<Long> convertNumList = Lists.newArrayList();
        List<Double> convertRateList = Lists.newArrayList();
        List<Long> convertSpuNumList = Lists.newArrayList();
        List<Double> convertSpuRateList = Lists.newArrayList();

        xdatas.forEach(x -> {
            //判断当前是否有数据
            QywxDailyStatis qywxDailyStatis = dailyStatisMap.get(x.format(DateTimeFormatter.ofPattern(dateFormat)));

            //找不到转化数据
            if (null == qywxDailyStatis) {
                if (x.isAfter(LocalDate.now()) || x.isEqual(LocalDate.now())) {
                    //填充空值
                    convertNumList.add(null);
                    convertRateList.add(null);
                    convertSpuNumList.add(null);
                    convertSpuRateList.add(null);
                } else {
                    //填充0
                    convertNumList.add(0L);
                    convertRateList.add(0D);
                    convertSpuNumList.add(0L);
                    convertSpuRateList.add(0D);
                }
            } else {
                convertNumList.add(qywxDailyStatis.getConvertNum());
                convertRateList.add(qywxDailyStatis.getConvertRate());
                convertSpuNumList.add(qywxDailyStatis.getConvertSpuNum());
                convertSpuRateList.add(qywxDailyStatis.getConvertSpuRate());
            }
        });

        result.put("xdata", xdatas.stream().map(x -> x.format(DateTimeFormatter.ofPattern(dateFormat))).collect(Collectors.toList()));
        result.put("ydata1", convertNumList);
        result.put("ydata2", convertRateList);
        result.put("ydata3", convertSpuNumList);
        result.put("ydata4", convertSpuRateList);
        return result;
    }

    @Override
    public QywxDailyStaffEffect getDailyStaffEffect(Long headId, String followUserId) {
        return qywxDailyDetailMapper.getDailyStaffEffect(headId, followUserId);
    }

    @Override
    public int getPushErrorCount(long headId) {
        return qywxDailyMapper.getPushErrorCount(headId);
    }

    @Override
    public void updateStatusToDoneCouponError(long headId) {
        qywxDailyMapper.updateStatusToDoneCouponError(headId);
    }

    @Override
    public void updateStatusToDonePushError(long headId) {
        qywxDailyMapper.updateStatusToDonePushError(headId);
    }

    @Override
    public synchronized String manualSubmitCoupon(long headId) {
        QywxDailyHeader header=qywxDailyMapper.getHeadInfo(headId);
        if(null!=header&&"N".equals(header.getCouponSendFlag()))
        {
            boolean flag=qywxSendCouponService.sendCouponToUser(headId);
            return flag?"发券成功":"发券失败";
        }else {
            return "不存在的ID或当前记录已发过券了";
        }
    }

    @Override
    public String manualSubmitMessage(long headId) {
        String appId = qywxMessageService.getMpAppId();
        //按导购分组
        List<FollowUserVO> followUserIdList =qywxDailyDetailMapper.getFollowUserList(headId);

        followUserIdList.forEach(x -> {
            String followUserId = x.getFollowUserId();
            // 推送消息(按消息分组)
            List<String> msgSignList =qywxDailyDetailMapper.getMessageSignList(headId,followUserId);

            //备注：同一msgSignList下必然是同一个商品
            msgSignList.forEach(y -> {

                //查询当前签名、当前导购下记录的条数
                int waitCount=qywxDailyDetailMapper.getWaitQywxUserListCount(headId,followUserId,y);
                int pageSize = 10000;
                if (waitCount <= pageSize) {
                    log.info("当前推送数据量<=10000");
                    if(waitCount > 0) {
                        //获取当前待推送的列表
                        List<QywxDailyDetail> qywxDailyDetailList=qywxDailyDetailMapper.getQywxUserList(headId,followUserId,y,waitCount,0);

                        QywxPushList qywxPushList=new QywxPushList();
                        qywxPushList.setTextContent(qywxDailyDetailList.get(0).getTextContent());
                        qywxPushList.setMpTitle(qywxDailyDetailList.get(0).getMpTitle());
                        qywxPushList.setMpUrl(qywxDailyDetailList.get(0).getMpUrl());
                        qywxPushList.setMpMediaId(qywxDailyDetailList.get(0).getMpMediaId());
                        qywxPushList.setMpAppid(appId);
                        qywxPushList.setExternalContactIds(StringUtils.join(qywxDailyDetailList.stream().map(QywxDailyDetail::getQywxContractId).collect(Collectors.toList()),","));
                        qywxPushList.setFollowUserId(followUserId);
                        qywxPushList.setSourceId(qywxDailyDetailList.get(0).getHeadId());
                        qywxDailyMapper.insertPushList(qywxPushList);
                        //推送并更新状态
                        try {
                            pushQywxMsg(qywxPushList,qywxDailyDetailList);
                        } catch (Exception e) {
                            new LinkSteadyException("推送任务出现异常！"+e);
                        }
                    }
                } else {
                    int pageNum = waitCount % pageSize == 0 ? (waitCount / pageSize) : ((waitCount / pageSize) + 1);
                    for (int i = 0; i < pageNum; i++) {
                        log.info("当前文本推送条数{}，偏移量为{}", pageSize,i*pageSize);
                        List<QywxDailyDetail> tmpUserList = qywxDailyDetailMapper.getQywxUserList(headId,followUserId,y,pageSize,i * pageSize);
                        if(tmpUserList.size() > 0) {

                            QywxPushList qywxPushList=new QywxPushList();
                            qywxPushList.setTextContent(tmpUserList.get(0).getTextContent());
                            qywxPushList.setMpTitle(tmpUserList.get(0).getMpTitle());
                            qywxPushList.setMpUrl(tmpUserList.get(0).getMpUrl());
                            qywxPushList.setMpMediaId(tmpUserList.get(0).getMpMediaId());
                            qywxPushList.setMpAppid(appId);
                            qywxPushList.setExternalContactIds(StringUtils.join(tmpUserList.stream().map(QywxDailyDetail::getQywxContractId).collect(Collectors.toList()),","));
                            qywxPushList.setFollowUserId(followUserId);
                            qywxPushList.setSourceId(tmpUserList.get(0).getHeadId());
                            qywxDailyMapper.insertPushList(qywxPushList);
                            //推送并更新状态
                            try {
                                pushQywxMsg(qywxPushList,tmpUserList);
                            } catch (Exception e) {
                                new LinkSteadyException("推送任务出现异常！"+e);
                            }
                        }
                    }
                }
            });
        });

        return "success";
    }

    @Override
    public List<QywxDailyPersonal> getConvertDetailData(int limit, int offset, long headId) {
        return qywxDailyMapper.getConvertDetailData(limit,offset,headId);
    }

    @Override
    public int getConvertDetailCount(long headId) {
        return qywxDailyMapper.getConvertDetailCount(headId);
    }


}
