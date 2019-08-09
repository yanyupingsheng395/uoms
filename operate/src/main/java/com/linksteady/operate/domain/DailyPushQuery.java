package com.linksteady.operate.domain;

import lombok.Data;

/**
 * @author hxcao
 * @date 2019-07-31
 */
@Data
public class DailyPushQuery {


    /**
     * 头表ID
     */
    private Long headId;

    private Long dailyDetailId;

    private String userId;

    private String userIdentify;

    private String smsCode;

    private String smsContent;

    private String recLastId;

    private String recLastName;

    private String recLastLongurl;

    private Long couponId;

    private String couponName;

    private String couponUrl;

    private Long growthStreadyId;
}