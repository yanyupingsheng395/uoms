package com.linksteady.qywx.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class QywxChatBatchMsg {
    /**
     * 主键ID
     */
    private long batchMsgId;
    /**
     * 群发消息名称
     */
    private String batchMsgName;
    /**
     * 群主列表
     */
    private String chatOwnerList;
    /**
     * 文本内容
     */
    private String textContent;
    /**
     * 图片链接
     */
    private String imgUrl;
    /**
     * 图文消息标题
     */
    private String linkTitle;
    /**
     * 图文消息封面URL
     */
    private String linkPicUrl;
    /**
     * 图文消息的描述
     */
    private String linkDesc;
    /**
     * 图文消息的连接
     */
    private String linkUrl;
    /**
     * 小程序标题
     */
    private String mpTitle;
    /**
     * 小程序路径
     */
    private String mpUrl;
    /**
     * 小程序封面MediaiD
     */
    private String mpMediaId;
    /**
     * 发送类型 A立即  B定时
     */
    private String sendType;
    /**
     * 定时执行的时间  当send_type为B时，此字段有值。
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date fixDate;
    /**
     * 状态 'todo' 草稿 'done' 已执行
     */
    private String status;
    /**
     * 创建人
     */
    private String insertBy;
    /**
     * 修改人
     */
    private String updateBy;
    /**
     * 插入时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date insertDt;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date updateDt;
    /**
     * 执行人
     */
    private String execBy;
    /**
     * 执行时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date execDate;
    /**
     * 群主数
     */
    private long chatOwnerSize;
    /**
     * 群聊数
     */
    private long chatSize;
    /**
     * 需要推送用户数
     */
    private long userSize;
    /**
     * 实际执行群主数
     */
    private long execChatOwnerSize;
    /**
     * 成功推送群数
     */
    private long execChatSize;
    /**
     * 成功推送用户数
     */
    private long execUserSize;

}
