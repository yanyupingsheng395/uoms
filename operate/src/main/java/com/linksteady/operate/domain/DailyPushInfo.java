package com.linksteady.operate.domain;

import lombok.Data;

/**
 * @author hxcao
 * @date 2019-07-31
 */
@Data
public class DailyPushInfo {

    private Long dailyDetailId;

    private String smsContent;

    private Long growthStreadyId;

    private String userId;

    private String userPhone;

    private String userOpenid;
}
