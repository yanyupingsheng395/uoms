package com.linksteady.system.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "t_role")
@Data
public class Role implements Serializable {

	private static final long serialVersionUID = -1714476694755654924L;

	@Id
	@Column(name = "ROLE_ID",insertable = false)
	private Long roleId;

	@Column(name = "ROLE_NAME")
	private String roleName;

	@Column(name = "REMARK")
	private String remark;

	@Column(name = "CREATE_DT")
	private Date createDt;

	@Column(name = "UPDATE_DT")
	private Date updateDt;

	@Column(name = "CREATE_BY")
	private String createBy;

	@Column(name = "UPDATE_BY")
	private String updateBy;

}