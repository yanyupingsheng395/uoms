package com.linksteady.operate.sms.domain;

import lombok.Data;

import java.util.List;

/**
 * 2 种实现方式
 *
 * 1.通过在生产端生产数据，在消费端手动消费
 *
 * 2.通过rocketMQ生产消费数据
 *
 * @author hxcao
 * @date 2019-09-12
 */
@Data
public class TaskInfo {

    private String taskId;

    private String taskName;

    private List<SmsInfo> smsInfoList;

    private String createTime;

    private String status;

    /**
     * 成功发送数
     */
    private String successNum;

    /**
     * 失败发送数
     */
    private String failedNum;

    /**
     * 1. 单条发送 single
     * 2. 相同内容群发 batch
     * 3. 个性化群发 multi
     */
    private String sendMsgType;

    /**
     * 是否启停
     */
    private String isStop;

    /**
     * 立即执行:now
     * 触发时段触发执行:scheduled
     */
    private String runType;
}