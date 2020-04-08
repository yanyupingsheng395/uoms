package com.linksteady.common.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Table(name = "t_log")
@Data
public class SysLog implements Serializable {

	private static final long serialVersionUID = -8878596941954995444L;

	@Id
	@Column(name = "ID",insertable = false)
	private Long id;

	@Column(name = "USERNAME")
	private String username;

	@Column(name = "OPERATION")
	private String operation;

	@Column(name = "TIME")
	private Long time;

	@Column(name = "METHOD")
	private String method;

	@Column(name = "PARAMS")
	private String params;

	@Column(name = "IP")
	private String ip;

	@Column(name = "CREATE_TIME")
	private Date createTime;

	@Column(name = "LOCATION")
	private String location;

	/**
	 * 用于搜索条件中的时间字段
	 */
	@Transient
	private String timeField;

	/**
	 * 日志类型 如果为page 表示功能访问日志，会保存到数据库，否则即保存到数据库，又保存到日志文件
	 */
	@Transient
	private LogTypeEnum logType;

	@Override
	public String toString() {
		return "{" +
				"id=" + id +
				", username='" + username + '\'' +
				", operation='" + operation + '\'' +
				", time=" + time +
				", method='" + method + '\'' +
				", params='" + params + '\'' +
				", ip='" + ip + '\'' +
				", createTime=" + createTime +
				", location='" + location + '\'' +
				", timeField='" + timeField + '\'' +
				", logType=" + logType.getDesc() +
				'}';
	}
}