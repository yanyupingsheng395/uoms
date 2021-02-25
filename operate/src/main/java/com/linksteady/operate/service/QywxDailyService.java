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
    void push(QywxDailyHeader qywxDailyHeader,Long effectDays,int version) throws Exception;

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

    List<QywxDailyPersonalEffect> getConvertDetailData(int limit, int offset, long headId);

    int getConvertDetailCount(long headId);

    /**
     * 每日运营任务失效
     */
    void expireActivityDailyHead();

    /**
     * 版本号加一
     * @param headId
     * @param version
     * @return
     */
    int updateVersion(Long headId, int version);

    /**
     * 获取uo_qywx_coupon有效优惠券的数量
     * @return
     */
    int getCouponListCount();

    /**
     * 分页查询有效优惠券明细
     * @param limit
     * @param offset
     * @return
     */
    List<QywxCoupon> getCouponListData(int limit, int offset);

    /**
     * 根据券ID，券标识，查看券流水号数量
     * @param couponId
     * @param couponIdentity
     * @return
     */
    int viewCouponCount(Long couponId, String couponIdentity);

    /**
     * 根据券ID，券标识，查看券流水号
     * @param limit
     * @param offset
     * @param couponId
     * @param couponIdentity
     * @return
     */
    List<couponSerialNo> viewCouponData(int limit, int offset, Long couponId, String couponIdentity);
}
