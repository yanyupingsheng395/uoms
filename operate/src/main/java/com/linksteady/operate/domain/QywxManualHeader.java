package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class QywxManualHeader {
    /**
     * 头表ID
     */
    private long headId;
    /**
     * 用成员数
     */
    private long userNumber;
    /**
     * 需要推送消息的成员数
     */
    private long totalNum;
    /**
     * 实际推送完成的用户数
     */
    private long successNum;
    /**
     * 实际执行推送的成员数
     */
    private long convertNum;
    /**
     * 任务状态（待推送  已推送）
     */
    private String status;
    /**
     * 推送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date pushDate;
    /**
     * 创建人
     */
    private String insertBy;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date insertDt;
    /**
     * 更新人
     */
    private String updateBy;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date updateDt;
    /**
     * 文本内容
     */
    private String textContent;
    /**
     * 小程序url
     */
    private String mpUrl;
    /**
     * 小程序标题
     */
    private String mpTitle;
    /**
     * 小程序封面ID
     */
    private String mpMediald;

}
