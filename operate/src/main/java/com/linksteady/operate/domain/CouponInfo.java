package com.linksteady.operate.domain;

import lombok.Data;

@Data
public class CouponInfo {
    private Integer couponId;
    private Integer couponDenom;
    private Integer couponThreshold;
    private String couponUrl;
    private String couponDesc;
    private String couponNum;
    private String couponInfo1;
    private String couponInfo2;
    private String couponDisplayName;
    private String validStatus;
    private String couponIdentity;//优惠券编号
    private Integer couponSn;//优惠券发放流水号

    /**
     * 优惠券来源：0： 智能，1：手动
     */
    private String couponSource;
    private String validEnd;
    private String checkFlag;
    private String checkComments;
    private String isSelected;
    private String isRec;
}