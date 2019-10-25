package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

@Data
public class PushLog {
    private Date logDate;

    private String logContent;

    /**
     * 推送日志类型  1表示推送记录 2表示防骚扰拦截记录
     */
    private String logType;
}
