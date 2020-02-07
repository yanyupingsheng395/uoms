package com.linksteady.operate.vo;

import lombok.Data;

@Data
public class GroupCouponVO {

    private String groupId;

    private String couponId;

    private String couponDisplayName;

    private double couponDenom;

    private double couponThreshold;

    private String couponUrl;

    public GroupCouponVO(String groupId, String couponId, String couponDisplayName, double couponDenom, double couponThreshold, String couponUrl) {
        this.groupId = groupId;
        this.couponId = couponId;
        this.couponDisplayName = couponDisplayName;
        this.couponDenom = couponDenom;
        this.couponThreshold = couponThreshold;
        this.couponUrl = couponUrl;
    }
}
