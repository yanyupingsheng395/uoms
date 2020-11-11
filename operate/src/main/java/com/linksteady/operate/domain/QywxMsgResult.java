package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

@Data
public class QywxMsgResult implements java.io.Serializable{

    private String msgId;

    private String externalUserId;

    private String chatId;

    private String followUserId;

    private int status;

    private String sendTime;

    private Date sendTimeDt;
}



