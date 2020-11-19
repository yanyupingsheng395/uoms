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

    public QywxContactWay()
    {
        
    }

    public QywxContactWay(Long contactWayId, String qrCode, String contactType, String state,
                          String usersList, String shortUrl, int externalUserNum, String createDt, String remark)
    {
         this.contactWayId=contactWayId;
         this.qrCode=qrCode;
         this.contactType=contactType;
         this.state=state;
         this.usersList=usersList;
         this.shortUrl=shortUrl;
         this.externalUserNum=externalUserNum;
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
