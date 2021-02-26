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
public class QywxContactWay {

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
     * 渠道活码名称
     */
    private String contactName;
    /**
     * 标签集合
     */
    private String tagIds;


    public QywxContactWay()
    {
        
    }
}
