package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

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

    //状态
    private String pushStatus;
    //供应商返回状态码
    private String pushCallbackCode;
    //发送时间
    private Date pushDate;
}
