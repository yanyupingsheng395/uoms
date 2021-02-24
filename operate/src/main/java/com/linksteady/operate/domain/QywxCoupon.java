package com.linksteady.operate.domain;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class QywxCoupon implements Serializable {
    private Long couponId;
    /**
     * 优惠券面额
     */
    private long couponDenom;
    /**
     * 优惠券门槛
     */
    private long couponThreshold;
    /**
     * 优惠券小程序地址
     */
    private String couponUrl;
    /**
     * 优惠券描述
     */
    private String couponDesc;
    /**
     * 优惠券数量
     */
    private long couponNum;
    /**
     * 有效期截止时间
     */
    private LocalDate validEnd;
    /**
     * 优惠券名称
     */
    private String couponDisplayName;
    /**
     * 是否有效标记 Y有效 N无效
     */
    private String validStatus;
    /**
     * 折扣等级
     */
    private String discountLevel;
    /**
     * 校验标记 1通过 0 不通过
     */
    private String checkFlag;
    /**
     * 校验描述
     */
    private String checkComments;
    /**
     * 商城优惠券唯一标记
     */
    private String couponIdentity;
    /**
     * 优惠券发放流水号
     */
    private long couponSn;
    /**
     * 优惠券对应h5地址
     */
    private String couponLongUrl;
    /**
     * 对应uo_coupon_serial_no的数量
     */
    private long couponSerialNum;

}
