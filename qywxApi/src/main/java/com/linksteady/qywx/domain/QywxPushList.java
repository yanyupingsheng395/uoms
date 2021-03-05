package com.linksteady.qywx.domain;

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
    private String followUserId;
    private Date insertDt;
    private String msgid;
    private String mpTitle;
    private String mpUrl;
    private String mpAppid;
    private String mpMediaId;
    private String externalContactIds;
    private String failList;
    /**
     *图片url
     */
    private String picUrl;
    /**
     *图文消息标题
     */
    private String linkTitle;
    /**
     *图文消息封面的url
     */
    private String linkUrl;
    /**
     *图文消息的描述
     */
    private String linkDesc;
    /**
     *图文消息的链接
     */
    private String linkPicurl;
}