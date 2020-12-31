package com.linksteady.qywx.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class QywxContactWayChat {
    /**
     * 渠道活码ID
     */
    private long contactWayId;
    /**
     * 群聊ID
     */
    private String chatId;
    /**
     * 群聊名称
     */
    private String chatName;
    /**
     * 入群二维码图片地址
     */
    private String chatQrimgUrl;
    /**
     * 新增时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date insertDt;
    /**
     * 新增人
     */
    private String insertBy;
}
