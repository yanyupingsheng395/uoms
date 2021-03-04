package com.linksteady.qywx.domain;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 *配置了客户联系功能的成员列表
 */
@Data
@Table(name ="uo_qywx_follower_user_list")
public class FollowUser implements Serializable {

   @Column(name="user_id")
   private String userId;

   @Column(name="name")
   private String name;

    @Column(name="department")
   private String department;

    @Column(name="user_order")
   private String userOrder;

    @Column(name="position")
   private String position;

    @Column(name="mobile")
   private String mobile;

    @Column(name="gender")
   private String  gender;

    @Column(name="email")
   private String email;

   @Column(name="is_leader_in_dept")
   private String leaderInDept;

    @Column(name="avatar")
   private String avatar;

   @Column(name = "thumb_avatar")
   private String thumbAvatar;

    @Column(name="telephone")
   private String telephone;

    @Column(name="alias")
   private String alias;

    @Column(name="address")
   private String address;

   @Column(name="open_userid")
   private String openUserid;

   @Column(name="main_department")
   private String mainDepartment;

    @Column(name="extattr")
   private String extattr;

    @Column(name="status")
   private String status;

   @Column(name="qr_code")
   private String qrCode;

   @Column(name="external_position")
   private String externalPosition;

   @Column(name="external_profile")
   private String externalProfile;

    @Column(name="operate_flag")
    private String operateFlag;

    @Column(name="insert_dt")
    private Date insertDt;

    @Column(name="update_dt")
    private Date updateDt;

    public FollowUser buildFromJsonObject(JSONObject jsonObject)
    {
        if(null!=jsonObject)
        {
            this.setUserId(jsonObject.getString("userid"));
            this.setName(jsonObject.getString("name"));
            this.setDepartment(jsonObject.getString("department"));
            this.setUserOrder(jsonObject.getString("user_order"));
            this.setPosition(jsonObject.getString("position"));
            this.setMobile(jsonObject.getString("mobile"));
            this.setGender(jsonObject.getString("gender"));
            this.setEmail(jsonObject.getString("email"));
            this.setLeaderInDept(jsonObject.getString("is_leader_in_dept"));
            this.setAvatar(jsonObject.getString("avatar"));
            this.setTelephone(jsonObject.getString("telephone"));
            this.setAlias(jsonObject.getString("alias"));
            this.setAddress(jsonObject.getString("address"));
            this.setOpenUserid(jsonObject.getString("open_userid"));
            this.setMainDepartment(jsonObject.getString("main_department"));
            this.setExtattr(jsonObject.getString("extattr"));
            this.setStatus(jsonObject.getString("status"));
            this.setQrCode(jsonObject.getString("qr_code"));
            this.setExternalPosition(jsonObject.getString("external_position"));
            this.setExternalProfile(jsonObject.getString("external_profile"));
        }
        return this;
    }
}
