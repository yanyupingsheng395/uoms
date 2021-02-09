package com.linksteady.operate.service;

import com.linksteady.operate.vo.CouponInfoVO;
import com.linksteady.operate.vo.SendCouponVO;

import java.util.List;

/**
 * 优惠券的发放
 */
public interface QywxSendCouponService {

    /**
     * 完成每日运营给定headId下优惠券的发放
     * @param headId
     */
    boolean sendCouponToDailyUser(Long headId);

    boolean sendCouponToUser(Long couponId,String couponIdentity,String userIdentity);

    /**
     *批量发券
     * @return
     */
    boolean sendCouponBatch(CouponInfoVO couponInfoVO, List<SendCouponVO> sendCouponVOList);

//    boolean sendList();
}
