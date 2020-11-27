package com.linksteady.qywx.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 *登录用户列表
 */
@Data
@Table(name = "qywx_user")
public class QywxUser implements Serializable {

    @Id
    @Column(name = "ID",insertable = false)
    private Long id;

    /**
     * 企业微信的userId
     */
    @Column(name="USER_ID")
    private String userId;

    @Column(name="USER_NAME")
    private String userName;

    @Column(name="AVATAR")
    private String avatar;

    @Column(name="CORP_ID")
    private String corpId;

    @Column(name="GENDER")
    private String gender;

    @Column(name="QR_CODE")
    private String qrCode;

    @Column(name="IS_ADMIN")
    private String isAdmin;

    @Column(name="INSERT_DT")
    private Date insertDt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @Column(name="UPDATE_DT")
    private Date updateDt;

    @Transient
    private String corpName;
}
