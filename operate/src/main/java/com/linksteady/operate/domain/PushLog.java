package com.linksteady.operate.domain;

import lombok.Data;

import javax.persistence.Transient;
import java.util.Date;

@Data
public class PushLog {

    /**
     * 涉及的用户数
     */
    private Long userCount;

    private Date logDate;

    private String logContent;

    /**
     * 推送日志类型  1表示推送记录 2表示防骚扰拦截记录
     */
    private String logType;

    @Transient
    private String logDateStr;

}
