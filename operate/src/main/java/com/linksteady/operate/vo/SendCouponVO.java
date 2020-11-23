package com.linksteady.operate.vo;

import lombok.Data;

@Data
public class SendCouponVO implements java.io.Serializable{

    /**
     * 业务上的主键
     */
    private long businessId;

    /**
     * 用户手机号
     */
    private String userPhone;

    /**
     * 用户微信生态下的unionid
     */
    private String unionId;

    /**
     * 给当前用户发送优惠券 生成的优惠券号
     */
    private String couponSn;

}
