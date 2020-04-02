package com.linksteady.operate.domain;

import lombok.Data;

/**
 * @author hxcao
 * @date 2020/3/16
 */
@Data
public class MsgInfo {

    private String msgId;

    private String msgTitle;

    private String msgContent;

    private String readFlag;

    private String readBy;

    private String readDt;

    private String typeName;

    private String warnFlag;

    private String createDt;

    private String createBy;

    private String msgLevelDesc;
}
