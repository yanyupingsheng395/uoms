package com.linksteady.wxofficial.entity.po;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2020/4/24
 */
@Data
public class WxFollowReply {

    private int id;

    private String msgType;

    private byte[] content;

    private String mediaId;

    private String createBy;

    private Date createDt;

    private String updateBy;

    private Date updateDt;

    private String contentStr;
}