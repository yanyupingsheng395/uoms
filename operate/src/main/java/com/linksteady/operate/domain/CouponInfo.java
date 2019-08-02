package com.linksteady.operate.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "UO_OP_COUPON")
public class CouponInfo {
    /**
     * 优惠券ID
     */
    @Column(name = "COUPON_ID")
    private Integer couponId;

    /**
     * 优惠券面额
     */
    @Column(name = "COUPON_DENOM")
    private Integer couponDenom;

    /**
     * 优惠券阀值
     */
    @Column(name = "COUPON_THRESHOLD")
    private Integer couponThreshold;

    /**
     * 优惠券名称
     */
    @Column(name = "COUPON_NAME")
    private String couponName;

    /**
     * 优惠券链接
     */
    @Column(name = "COUPON_URL")
    private String couponUrl;
}
