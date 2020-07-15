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

    private Long id;

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

    private String shortUrl="dwz.cn/FJWX8n";

    public QywxContactWay()
    {
        
    }

    public QywxContactWay(Long id,String qrCode,String type,String remark,String state,String userList,String configId)
    {
        this.id=id;
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
        return (int)(this.getCreateDt().getTime()-o.getCreateDt().getTime());
    }
}
