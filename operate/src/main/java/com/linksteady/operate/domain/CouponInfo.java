package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    private String couponDesc;

    private  String couponType;

    private String couponNum;

    private String couponInfo1;

    private String couponInfo2;

    private String couponDisplayName;

    private String validStatus;

    private String validEnd;

    /**
     * 优惠券来源：0： 智能，1：手动
     */
    private String couponSource;

    private String userValue;
    private String pathActive;
    private String lifeCycle;

    private String checkFlag;
    private String checkComments;
}