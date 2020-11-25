package com.linksteady.qywx.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2019-07-31
 */
@Data
public class PushListInfo {

    private Long pushId;

    private String pushContent;

    private String pushPeriod;

    //状态
    private String pushStatus;

    private String callbackId;

    //供应商返回状态码
    private String callbackCode;

    private String callbackDesc;

    private String sourceCode;

    private Long sourceId;

    private String userPhone;

    private String userOpenId;

    //发送时间
    private Date pushDate;

    private String pushDateStr;
}
