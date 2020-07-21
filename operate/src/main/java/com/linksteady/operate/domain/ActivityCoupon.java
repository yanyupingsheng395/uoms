package com.linksteady.operate.domain;

import lombok.Data;

/**
 * @author hxcao
 * @date 2020/7/20
 */
@Data
public class ActivityCoupon {
    private long activityCouponId;
    private long headId;
    private String couponThreshold;
    private String couponDenom;
    private String couponType;
    private String addFlag;
}
