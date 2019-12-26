package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2019-10-23
 */
@Data
public class PushListLarge {

    private String pushContent;
    private String pushStatus;
    private String callbackCode;
    private String callbackDesc;
    private String sourceCode;
    private Long sourceId;
    private String msgid;
    private Date pushDate;
    private String userPhone;
    private String userOpenid;
    private String isPush;
    private Long pushSchedulingDate;
    private Long pushId;

}
