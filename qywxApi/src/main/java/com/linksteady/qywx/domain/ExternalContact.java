package com.linksteady.qywx.domain;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;
@Data
public class ExternalContact implements Serializable {

    private String externalUserid;

    private String name;

    private String position;

    private String avatar;

    private String corpName;

    private String corpFullName;

    private String type;

    private String gender;

    private String unionid;

    private String externalProfile;

    private String followUser;

    private String corpId;

    private String followerUserId;

    private String remark;

    private String description;

    private String createtime;

    private String tags;

    private String remarkCorpName;

    private String remarkMobiles;

    private String state;

    private Date addDate;
}
