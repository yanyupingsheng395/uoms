package com.linksteady.common.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
@Table(name = "t_user")
public class User implements Serializable {

	private static final long serialVersionUID = -4852732617765810959L;
	/**
	 * 账户状态
	 */
	public static final String STATUS_VALID = "1";

	public static final String STATUS_LOCK = "0";

	public static final String DEFAULT_THEME = "green";

	public static final String DEFAULT_AVATAR = "default.jpg";

	/**
	 * 性别
	 */
	public static final String SEX_MALE = "0";

	public static final String SEX_FEMALE = "1";

	public static final String SEX_UNKNOW = "2";

	@Id
	@GeneratedValue(generator = "JDBC")
	@Column(name = "USER_ID")
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

	@Column(name = "CRATE_TIME")
	private Date crateTime;

	@Column(name = "MODIFY_TIME")
	private Date modifyTime;

	@Column(name = "LAST_LOGIN_TIME")
	private Date lastLoginTime;

	@Column(name = "SSEX")
	private String ssex;

	@Column(name = "THEME")
	private String theme;

	@Column(name = "AVATAR")
	private String avatar;

	@Column(name = "DESCRIPTION")
	private String description;

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

	@Column(name = "FIRSTLOGIN")
	private String firstLogin ="Y";

	/**
	 * 用户所拥有的菜单 key:sysId
	 */
	@Transient
	private Map<String, Tree<Menu>> userMenuTree;
}