package com.linksteady.operate.service;

/**
 * 优惠券的发放
 */
public interface QywxSendCouponService {

    /**
     * 完成每日运营给定headId下优惠券的发放
     * @param headId
     */
    boolean sendCouponToDailyUser(Long headId);

    boolean sendCouponToUser();
}
