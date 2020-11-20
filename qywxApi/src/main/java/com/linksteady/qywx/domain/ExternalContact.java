package com.linksteady.qywx.domain;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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

    @Column(name = "relation")
    private String relation;

    @Column(name = "loss")
    private String loss;

    @Column(name = "stage_value")
    private String stageValue;

    @Column(name = "touch_interval")
    private String interval;

    @Column(name = "add_way")
    private int addWay;

    @Column(name = "oper_userid")
    private String operUserId;

    @Column(name="mobile")
    private String mobile;


    /**
     * 手机号是否维护的标记
     */
    @Transient
    private String fixFlag;

    /**
     * 获取批量用户的详细信息使用此接口构造
     * @param jsonObject
     * @param followerUserId
     * @return
     */
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
            this.setFollowUser(jsonObject.getString("follow_info"));
            JSONObject followInfo = jsonObject.getJSONObject("follow_info");

            this.setRemark(followInfo.getString("remark"));
            this.setDescription(followInfo.getString("description"));
            this.setCreatetime(followInfo.getString("createtime"));
            this.setTags(followInfo.getString("tags"));
            this.setRemarkCorpName(followInfo.getString("remark_corp_name"));
            this.setRemarkMobiles(followInfo.getString("remark_mobiles"));
            this.setState(followInfo.getString("state"));

            this.setAddWay(followInfo.getIntValue("add_way"));
            this.setOperUserId(followInfo.getString("oper_userid"));

            JSONArray remarkMobiles=followInfo.getJSONArray("remark_mobiles");
            if(null!=remarkMobiles&&remarkMobiles.size()>0)
            {
                for(int m=0;m<remarkMobiles.size();m++)
                {
                    if(StringUtils.isNotEmpty(remarkMobiles.getString(m)))
                    {
                        this.setMobile(remarkMobiles.getString(m));
                    }
                }
            }
        }
        this.setFollowerUserId(followerUserId);
        return this;
    }

    /**
     * 获取单个用户的详细信息使用此构造方法
     * @param jsonObject
     * @param followerUserId
     * @return
     */
    public ExternalContact buildFromJsonObjectSingle(JSONObject jsonObject,String followerUserId) {
        if(null!=jsonObject)
        {
            JSONObject externalContract=jsonObject.getJSONObject("external_contact");

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

            JSONArray jsonArray=jsonObject.getJSONArray("follow_user");
            jsonArray.stream().forEach(i->{
                JSONObject temp=(JSONObject)i;
                //获取当前企业员工打上的标签和备注
                if(followerUserId.equals(temp.getString("userid")))
                {
                    this.setRemark(temp.getString("remark"));
                    this.setDescription(temp.getString("description"));
                    this.setCreatetime(temp.getString("createtime"));
                    this.setTags(temp.getString("tags"));
                    this.setRemarkCorpName(temp.getString("remark_corp_name"));
                    this.setRemarkMobiles(temp.getString("remark_mobiles"));
                    this.setState(temp.getString("state"));

                    this.setAddWay(temp.getIntValue("add_way"));
                    this.setOperUserId(temp.getString("oper_userid"));

                    JSONArray remarkMobiles=temp.getJSONArray("remark_mobiles");
                    if(null!=remarkMobiles&&remarkMobiles.size()>0)
                    {
                        for(int m=0;m<remarkMobiles.size();m++)
                        {
                            if(StringUtils.isNotEmpty(remarkMobiles.getString(m)))
                            {
                                this.setMobile(remarkMobiles.getString(m));
                            }
                        }
                    }
                }
            });
        }

        this.setFollowerUserId(followerUserId);
        return this;
    }



}
