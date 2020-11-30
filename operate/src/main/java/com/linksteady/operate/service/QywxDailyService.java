package com.linksteady.operate.service;

import com.linksteady.operate.domain.*;

import java.util.List;
import java.util.Map;

/**
 * @author huang
 * @date 2020-05-12
 */
public interface QywxDailyService {

    List<QywxDailyHeader> getHeadList(int limit, int offset, String taskDate);

    int getTotalCount(String touchDt);

    /**
     * 获取生成文案的锁  true表示加锁成功 false表示加锁失败
     */
    boolean getTransContentLock(String headId);

    /**
     * 释放生成文案的锁
     */
    void delTransLock();

    /**
     * 获取任务头信息
     */
    QywxDailyHeader getHeadInfo(Long headId);

    /**
     * 企业微信消息推送
     * @param qywxDailyHeader
     * @param effectDays
     * @throws Exception
     */
    void push(QywxDailyHeader qywxDailyHeader,Long effectDays) throws Exception;

    /**
     * 获取推送变化数据
     * @param headId
     * @return
     */
    Map<String, Object> getPushEffectChange(Long headId);

    QywxDailyStaffEffect getDailyStaffEffect(Long headId, String followUserId);

    /**
     * 查看企业微信消息的执行状态
     */
    int getPushErrorCount(long headId);

    /**
     * 更新状态为已执行但发券错误
     */
    void updateStatusToDoneCouponError(long headId);

    /**
     * 更新状态为已执行但推送消息错误
     */
    void updateStatusToDonePushError(long headId);


    /**
     * 手工发送优惠券
     */
    String manualSubmitCoupon(long headId);


    /**
     * 手工发送消息
     */
    String manualSubmitMessage(long headId);

    List<QywxDailyPersonal> getConvertDetailData(int limit, int offset, long headId);

    int getConvertDetailCount(long headId);


}
