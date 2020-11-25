package com.linksteady.common.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@Data
@Table(name = "t_user")
public class User implements Serializable {

	private static final long serialVersionUID = -4852732617765810959L;
	/**
	 * 账户状态
	 */
	public static final String STATUS_VALID = "1";

	public static final String STATUS_LOCK = "0";

	@Id
	@Column(name = "USER_ID",insertable = false)
	private Long userId;

	@Column(name = "USERNAME")
	private String username;

	@Column(name = "PASSWORD")
	private String password;


	@Column(name = "EMAIL")
	private String email;

	@Column(name = "MOBILE")
	private String mobile;

	@Column(name = "STATUS")
	private String status = STATUS_VALID;

	@Column(name = "LAST_LOGIN_TIME")
	private Date lastLoginTime;

	@Transient
	private String roleName;

	@Column(name = "EXPIRE_DATE")
	@JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
	private Date expireDate;

	@Transient
	private String expire;

	@Column(name = "CREATE_BY")
	private String createBy;

	@Column(name = "UPDATE_BY")
	private String updateBy;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	@Column(name = "CREATE_DT")
	private Date createDt;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	@Column(name = "UPDATE_DT")
	private Date updateDt;

	@Column(name = "FIRSTLOGIN")
	private String firstLogin ="Y";

	/**
	 * 用户类型 默认为 PASS 用户名密码登录  其余值： QYWX 使用企业微信扫码登录 ALL 两者皆可登录
	 */
	@Column(name = "USER_TYPE")
	private String userType ="PASS";

	/**
	 * 用户所拥有的菜单 key:sysCode
	 */
	@Transient
	private Map<String, Tree<Menu>> userMenuTree;

	@Transient
	private Set<String> permissionSet;

}