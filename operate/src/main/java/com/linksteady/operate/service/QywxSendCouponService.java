package com.linksteady.operate.service;

import com.linksteady.operate.vo.CouponInfoVO;
import com.linksteady.operate.vo.SendCouponVO;

import java.util.List;

/**
 * 优惠券的发放
 */
public interface QywxSendCouponService {


    boolean sendCouponToUser(Long couponId,String couponIdentity,String userIdentity);

    /**
     *批量发券
     * @return
     */
    boolean sendCouponBatch(long couponId,CouponInfoVO couponInfoVO, List<SendCouponVO> sendCouponVOList);

}
