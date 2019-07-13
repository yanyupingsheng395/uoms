package com.linksteady.operate.domain;

import java.io.Serializable;

/**
 * @author hxcao
 * @date 2019-07-12
 */
public class JobBo implements Serializable {

    /**
     * 任务ID
     */
    private String jobId;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 请求的Bean名称
     */
    private String handler;

    /**
     * 阻塞策略
     * SERIAL_EXECUTION   串行
     * DISCARD_LATER      丢弃后续
     * COVER_EARLY        覆盖之前
     */
    private String executorBlockStrategy;

    /**
     * 方法参数
     */
    private String executorParams;

    /**
     * 执行超时时间
     */
    private String executorTimeout;

    /**
     * 任务开始时间 yyyy-MM-dd HH:mm:ss
     */
    private String logDateTime;

    /**
     * 任务日志ID
     */
    private String logId;
}
