package com.linksteady.operate.vo;

import lombok.Data;

@Data
public class SendCouponVO implements java.io.Serializable{

    /**
     * 业务上的主键
     */
    private long businessId;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 用户的标记
     */
    private String userIdentity;

    /**
     * 给当前用户发送优惠券 生成的优惠券号
     */
    private String couponSn;

}
