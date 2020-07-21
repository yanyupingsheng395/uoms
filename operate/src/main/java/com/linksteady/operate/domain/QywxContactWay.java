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

    private String contactType;

    private String scene;

    private String style;

    private String remark;

    private Boolean skipVerify=true;

    private String state;

    private String usersList;

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
    
    @Override
    public int compareTo(QywxContactWay o) {
        return (int)(o.getCreateDt().getTime()-this.getCreateDt().getTime());
    }
}
