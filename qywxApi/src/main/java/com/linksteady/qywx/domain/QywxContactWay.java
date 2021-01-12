package com.linksteady.qywx.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author huang
 * 渠道活码
 */
@Data
public class QywxContactWay implements Comparable<QywxContactWay>{

    private Long contactWayId;

    private String qrCode;

    private String contactType;

    private String scene;

    private String style;

    private String remark;

    private Boolean skipVerify=true;

    private String state;

    private String usersList;

    private String deptList;

    private String party;

    private String configId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date createDt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date updateDt;

    private String createBy;

    private String updateBy;

    /**
     * 已添加的用户数量
     */
    private int externalUserNum;

    /**
     * 对应的短链接
     */
    private String shortUrl;
    /**
     * 引导进群欢迎语
     */
    private String chatText;
    /**
     * 是否关联群聊
     */
    private String relateChat;
    /**
     * 渠道活码名称
     */
    private String contactName;
    /**
     * 标签集合
     */
    private String tagIds;

    /**
     *图片url
     */
    private String picUrl;
    /**
     *图文消息标题
     */
    private String linkTitle;
    /**
     *图文消息封面的url
     */
    private String linkUrl;
    /**
     *图文消息的描述
     */
    private String linkDesc;
    /**
     *图文消息的链接
     */
    private String linkPicurl;
    /**
     * 小程序标题
     */
    private String mpTitle;
    /**
     * 小程序url
     */
    private String mpUrl;
    /**
     * 小程序标题ID
     */
    private String mediaId;
    /**
     * 选择消息类型
     */
    private String msgType;

    /**
     * 用于接收前端传过来的群码关联列表集合
     */
    private String watChatList;

    public QywxContactWay()
    {
        
    }

    public QywxContactWay(Long contactWayId, String qrCode, String contactType, String state,
                          String usersList, String shortUrl, int externalUserNum, String createDt, String remark,String watChatList)
    {
         this.contactWayId=contactWayId;
         this.qrCode=qrCode;
         this.contactType=contactType;
         this.state=state;
         this.usersList=usersList;
         this.shortUrl=shortUrl;
         this.externalUserNum=externalUserNum;
         this.watChatList=watChatList;
        try {
            this.createDt=new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(createDt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.remark=remark;
    }
    
    @Override
    public int compareTo(QywxContactWay o) {
        return (int)(o.getCreateDt().getTime()-this.getCreateDt().getTime());
    }
}
