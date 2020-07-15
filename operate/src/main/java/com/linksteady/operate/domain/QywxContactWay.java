package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author huang
 * 渠道活码
 */
@Data
public class QywxContactWay implements Comparable<QywxContactWay>{

    private Long contactWayId;

    private String qrCode;

    private String type;

    private String scene;

    private String style;

    private String remark;

    private String skipVerify;

    private String state;

    private String usersList;

    private String party;

    private String configId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date createDt;

    private int externalUserNum;

    private String shortUrl="wz2.in/0K6i6C";

    public QywxContactWay()
    {
        
    }

    public QywxContactWay(Long contactWayId,String qrCode,String type,String remark,String state,String userList,String configId)
    {
        this.contactWayId=contactWayId;
        this.qrCode=qrCode;
        this.type=type;
        this.remark=remark;
        this.state=state;
        this.usersList=userList;
        this.configId=configId;
        this.createDt=new Date();
    }

    @Override
    public int compareTo(QywxContactWay o) {
        return (int)(o.getCreateDt().getTime()-this.getCreateDt().getTime());
    }
}
