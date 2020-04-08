package com.linksteady.system.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "t_user_role")
public class UserRole implements Serializable{
	
	private static final long serialVersionUID = -3166012934498268403L;

	@Column(name = "USER_ID")
	private Long userId;

	@Column(name = "ROLE_ID")
	private Long roleId;

	@Column(name = "CREATE_DT")
	private Date createDt;

	@Column(name = "CREATE_BY")
	private String createBy;

}