package com.linksteady.qywx.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QywxChatBase {
    /**
     * 群公告
     */
    private String notice;
    /**
     * 群主名称
     */
    private String owner;
    /**
     * 客户群ID
     */
    private String chatId;
    /**
     *群名称
     */
    private String groupName;
    /**
     * 客户群状态
     */
    private String status;

    /**
     * 今日退群人数
     */
    private long groupOut;
    /**
     * 今日入群人数
     */
    private long groupJoin;
    /**
     * 群总人数
     */
    private long groupNumber;
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
     * 微信群创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date createTime;


}
