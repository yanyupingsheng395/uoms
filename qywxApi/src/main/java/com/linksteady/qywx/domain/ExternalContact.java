package com.linksteady.qywx.domain;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * 外部联系人
 */
@Slf4j
@Data
@Table(name = "uo_qywx_external_user_list")
public class ExternalContact implements Serializable {

    @Column(name = "external_userid")
    private String externalUserid;

    @Column(name = "name")
    private String name;

    @Column(name = "position")
    private String position;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "corp_name")
    private String corpName;

    @Column(name = "corp_full_name")
    private String corpFullName;

    @Column(name = "type")
    private String type;

    @Column(name = "gender")
    private String gender;

    @Column(name = "unionid")
    private String unionid;

    @Column(name = "external_profile")
    private String externalProfile;

    @Column(name = "follow_user")
    private String followUser;

    @Column(name = "follow_user_id")
    private String followerUserId;

    @Column(name = "remark")
    private String remark;

    @Column(name = "description")
    private String description;

    @Column(name = "createtime")
    private String createtime;

    @Column(name = "tags")
    private String tags;

    @Column(name = "remark_corp_name")
    private String remarkCorpName;

    @Column(name = "remark_mobiles")
    private String remarkMobiles;

    @Column(name = "state")
    private String state;

    @Transient
    private String createTimeStr;

    @Column(name = "mapping_flag")
    private String mappingFlag;

    @Column(name = "operate_user_id")
    private String operateUserId;

    @Column(name = "mapping_date")
    private Date mappingDate;

    @Column(name = "follow_count")
    private Integer followCount;

    @Column(name = "relation")
    private String relation;

    @Column(name = "loss")
    private String loss;

    @Column(name = "stage_value")
    private String stageValue;

    @Column(name = "touch_interval")
    private String interval;

    /**
     * 手机号是否维护的标记
     */
    @Transient
    private String fixFlag;

    public ExternalContact buildFromJsonObject(JSONObject jsonObject,String followerUserId) {
        if (null != jsonObject) {
            JSONObject externalContract = jsonObject.getJSONObject("external_contact");

            this.setExternalUserid(externalContract.getString("external_userid"));
            this.setName(externalContract.getString("name"));
            this.setPosition(externalContract.getString("position"));
            this.setAvatar(externalContract.getString("avatar"));
            this.setCorpName(externalContract.getString("corp_name"));
            this.setCorpFullName(externalContract.getString("corp_full_name"));
            this.setType(externalContract.getString("type"));
            this.setGender(externalContract.getString("gender"));
            this.setUnionid(externalContract.getString("unionid"));
            this.setExternalProfile(externalContract.getString("external_profile"));

            this.setFollowUser(jsonObject.getString("follow_user"));

            JSONArray jsonArray = jsonObject.getJSONArray("follow_user");
            this.setFollowCount((int) jsonArray.stream().count());
            jsonArray.stream().forEach(i -> {
                JSONObject temp = (JSONObject) i;
                //获取当前企业员工打上的标签和备注
                if (followerUserId.equals(temp.getString("userid"))) {
                    this.setRemark(temp.getString("remark"));
                    this.setDescription(temp.getString("description"));
                    this.setCreatetime(temp.getString("createtime"));
                    this.setTags(temp.getString("tags"));
                    this.setRemarkCorpName(temp.getString("remark_corp_name"));
                    this.setRemarkMobiles(temp.getString("remark_mobiles"));
                    this.setState(temp.getString("state"));
                }
            });

            //todo 四个属性标签的默认值
        }
        this.setFollowerUserId(followerUserId);
        return this;
    }
}
