package com.linksteady.wxofficial.entity.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hxcao
 * @date 2020/4/22
 */
@Data
public class WxMpUserBo implements Serializable {
    private Boolean subscribe;
    private String openId;
    private String nickname;
    private String sexDesc;
    private Integer sex;
    private String language;
    private String city;
    private String province;
    private String country;
    private String headImgUrl;
    private Long subscribeTime;
    private String unionId;
    private String remark;
    private Integer groupId;
    private Long[] tagIds;
    private String[] privileges;
    private String subscribeScene;
    private String qrScene;
    private String qrSceneStr;
}