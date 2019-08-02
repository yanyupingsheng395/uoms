package com.linksteady.operate.domain;

import lombok.Data;

/**
 * @author hxcao
 * @date 2019-07-31
 */
@Data
public class DailyPushInfo {

    private Long pushListId;

    /**
     * ID
     */
    private Long groupId;

    /**
     * 头表ID
     */
    private Long headId;

    private Long dailyDetailId;

    private String userId;

    private String phoneNum;

    private String smsCode;

    private String smsContent;

    private String recLastId;

    private String recLastName;

    private Long couponId;

    private String couponName;

    private Long growthStreadyId;

    private String orderPeriod;

}
