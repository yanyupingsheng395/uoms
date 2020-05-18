package com.linksteady.wxofficial.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2020/4/29
 */
@Data
public class WxPushHead {

    private int id;

    private String isTotalUser;

    private String msgType;

    private String msgContent;

    private String status;

    private String tagId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date createDt;

    private String mediaId;
}
