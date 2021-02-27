package com.linksteady.qywx.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class QywxWelcome implements Serializable {
    private Long id;

    private String welcomeName;

    private String content;

    private String insertBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date insertDt;

    private String policyType;

    private String picUrl;

    private String picId;

    private String linkTitle;

    private String linkUrl;

    private String linkDesc;

    private String linkPicurl;

    private String miniprogramTitle;

    private String miniprogramPage;

    private String validFlag;

    private String status;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date updatetDt;

    private String msgType;
    /**
     *小程序图片名称
     */
    private String miniprogramImageName;
    /**
     *小程序图片路径
     */
    private String miniprogramImagePath;

}
