package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2019/12/25
 */
@Data
public class ManualHeader {

    private long headId;
    private String fileName;
    private long allNum;
    private long validNum;
    private long unvalidNum;
    private long successNum;
    private long faildNum;
    private long interceptNum;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date insertDt;
    private Date updateDt;
    private String insertBy;
    private String updateBy;
    private String smsContent;
    private String pushType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date scheduleDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date actualPushDate;
}
