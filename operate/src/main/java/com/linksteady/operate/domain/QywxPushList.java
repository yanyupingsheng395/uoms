package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2020/9/15
 */
@Data
public class QywxPushList {
    private long pushId;
    private String textContent;
    private String pushStatus;
    private String sourceCode;
    private long sourceId;
    private Date pushDate;
    private String qywxUserId;
    private Date insertDt;
    private String msgid;
    private String mpTitle;
    private String mpUrl;
    private String mpAppid;
    private String mpMediaId;
    private String externalContactIds;
    private String failList;
}