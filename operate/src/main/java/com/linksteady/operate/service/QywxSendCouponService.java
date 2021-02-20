package com.linksteady.operate.service;

import com.linksteady.operate.vo.CouponInfoVO;
import com.linksteady.operate.vo.SendCouponResultVO;
import com.linksteady.operate.vo.SendCouponVO;

import java.util.List;

/**
 * 优惠券的发放
 */
public interface QywxSendCouponService {


    SendCouponResultVO sendCouponToUser(CouponInfoVO couponInfoVO,SendCouponVO sendCouponVO) throws Exception;

    /**
     *批量发券
     * @return
     */
    SendCouponResultVO sendCouponBatch(CouponInfoVO couponInfoVO, List<SendCouponVO> sendCouponVOList)  throws Exception;

}
