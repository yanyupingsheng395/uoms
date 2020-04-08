package com.linksteady.common.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "t_menu")
public class Menu implements Serializable {


	private static final long serialVersionUID = 7187628714679791771L;

	public static final String TYPE_MENU = "0";

	public static final String TYPE_BUTTON = "1";

	@Id
	@Column(name = "MENU_ID",insertable = false)
	private Long menuId;

	@Column(name = "PARENT_ID")
	private Long parentId;

	@Column(name = "MENU_NAME")
	private String menuName;

	@Column(name = "URL")
	private String url;

	@Column(name = "PERMS")
	private String perms;

	@Column(name = "ICON")
	private String icon;

	@Column(name = "TYPE")
	private String type;

	@Column(name = "ORDER_NUM")
	private Long orderNum;

	@Column(name = "SYS_CODE")
	private String sysCode;

	@Transient
	private String sysName;

	@Column(name = "CREATE_DT")
	private Date createDt;

	@Column(name = "UPDATE_DT")
	private Date updateDt;

	@Column(name = "CREATE_BY")
	private String createBy;

	@Column(name = "UPDATE_BY")
	private String updateBy;
}