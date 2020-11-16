package com.linksteady.qywx.vo;

import com.linksteady.qywx.utils.xml.XStreamCDataConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import lombok.Data;

/**
 * 异步任务回调通知
 */
@Data
@XStreamAlias("BatchJob")
public class BatchJob implements java.io.Serializable{

    /**
     * 任务ID
     */
    @XStreamAlias("JobId")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String jobId;

    /**
     * 任务类型
     */
    @XStreamAlias("JobType")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String jobType;
}