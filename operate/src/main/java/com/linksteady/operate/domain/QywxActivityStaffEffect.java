package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class QywxActivityStaffEffect  implements Serializable {
    /**
     * 活动运营head_id
     */
    private Long activityHeadId;
    /**
     * 成员ID
     */
    private String followUserId;
    /**
     * 需执行的推送消息数
     */
    private long msgNum;
    /**
     * 实际执行的推送消息数
     */
    private long executeMsgNum;
    /**
     * 全部执行推送预计覆盖用户数
     */
    private long coverNum;
    /**
     * 实际执行推送后覆盖用户数
     */
    private long executeCoverNum;
    /**
     * 推送并转化用户数
     */
    private long convertNum;
    /**
     * 推送转化金额
     */
    private double convertAmount;
    /**
     * 推送转化率
     */
    private double convertRate;
    /**
     * 推送并购买SPU用户数
     */
    private long convertSpuNum;
    /**
     * 推送并购买SPU转化金额
     */
    private double convertSpuAmount;
    /**
     * 推送并购买SPU转化率
     */
    private double convertSpuRate;
    /**
     * 统计日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date statDate;
}
