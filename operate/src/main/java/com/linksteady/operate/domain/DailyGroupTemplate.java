package com.linksteady.operate.domain;

import lombok.Data;

/**
 * @author hxcao
 * @date 2019-09-20
 */
@Data
public class DailyGroupTemplate {

    private String groupId;

    private String groupName;

    private String smsCode;

    private String smsContent;

    /**
     * 是否包含优惠券
     */
    private String isCoupon;

    /**
     * N个优惠券的名称
     */
    private String couponName;

    /**
     * N个优惠券的ID
     */
    private String couponId;
}
