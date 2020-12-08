package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

@Data
public class QywxManualDetail {
    /**
     *活动明细ID
     */
    private Long detailId;
    /**
     * 头表ID
     */
    private Long headId;
    /**
     * 对应企业微信成员ID
     */
    private String followerUserId;
    /**
     * 对应企业微信客户ID
     */
    private String qywxContactId;
    /**
     * 执行状态 -999默认表示尚未更新 0-未发送 1-已发送
     */
    private Integer execStatus;
    /**
     * 执行时间
     */
    private Date execDate;
    /**
     * 消息ID
     */
    private String msgId;
    /**
     * 企业微信消息推送ID
     */
    private Long pushId;
    /**
     * 创建人
     */
    private String insertBy;
    /**
     * 创建时间
     */
    private Date insertDt;
    /**
     * 更新人
     */
    private String updateBy;
    /**
     * 更新时间
     */
    private Date updateDt;

}
