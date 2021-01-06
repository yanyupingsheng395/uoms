package com.linksteady.qywx.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class QywxChatDetail {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 用户类型
     */
    private String userType;
    /**
     * w唯一标识
     */
    private String unionid;
    /**
     * 加入时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date joinTime;
    /**
     * 加入方式
     */
    private String joinScene;
    /**
     * 主表ID
     */
    private String  chatId;
    /**
     * 用户名称
     */
    private String userName;

}
