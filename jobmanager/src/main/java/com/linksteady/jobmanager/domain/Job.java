package com.linksteady.jobmanager.domain;

import com.google.common.base.MoreObjects;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author hxcao
 * @date 2019-07-04
 */
@Table(name = "t_job")
public class Job implements Serializable {

    private static final long serialVersionUID = 400066840871805700L;

    /**
     * 任务调度参数key
     */
    public static final String JOB_PARAM_KEY = "JOB_PARAM_KEY";

    public enum ScheduleStatus {
        /**
         * 正常
         */
        NORMAL("0"),
        /**
         * 暂停
         */
        PAUSE("1");

        private String value;

        private ScheduleStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "JOB_ID")
    private Long jobId;

    @Column(name = "BEAN_NAME")
    private String beanName;

    @Column(name = "METHOD_NAME")
    private String methodName;

    @Column(name = "PARAMS")
    private String params;

    @Column(name = "CRON_EXPRESSION")
    private String cronExpression;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "REMARK")
    private String remark;

    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * @return JOB_ID
     */
    public Long getJobId() {
        return jobId;
    }

    /**
     * @param jobId
     */
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    /**
     * @return BEAN_NAME
     */
    public String getBeanName() {
        return beanName;
    }

    /**
     * @param beanName
     */
    public void setBeanName(String beanName) {
        this.beanName = beanName == null ? null : beanName.trim();
    }

    /**
     * @return METHOD_NAME
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * @param methodName
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName == null ? null : methodName.trim();
    }

    /**
     * @return PARAMS
     */
    public String getParams() {
        return params;
    }

    /**
     * @param params
     */
    public void setParams(String params) {
        this.params = params == null ? null : params.trim();
    }

    /**
     * @return CRON_EXPRESSION
     */
    public String getCronExpression() {
        return cronExpression;
    }

    /**
     * @param cronExpression
     */
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression == null ? null : cronExpression.trim();
    }

    /**
     * @return STATUS
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    /**
     * @return REMARK
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * @return CREATE_TIME
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("jobId", jobId)
                .add("beanName", beanName)
                .add("methodName", methodName)
                .add("params", params)
                .add("cronExpression", cronExpression)
                .add("status", status)
                .add("remark", remark)
                .add("createTime", createTime)
                .toString();
    }
}