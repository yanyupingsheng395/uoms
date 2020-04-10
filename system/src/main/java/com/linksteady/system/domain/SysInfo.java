package com.linksteady.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author hxcao on 2019-05-05
 */
@Data
@Table(name = "T_SYSTEM")
public class SysInfo {

    @Id
    @Column(name = "ID",insertable = false)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CODE")
    private String code;

    @Column(name = "ENABLE_FLAG")
    private String enableFlag;

    @Column(name = "DOMAIN")
    private String domain;

    @Column(name = "LOGO")
    private String logo;

    @Column(name = "SORT_NUM")
    private Long sortNum;

    @Column(name = "REMARK")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @Column(name = "CREATE_DT")
    private Date createDt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @Column(name = "UPDATE_DT")
    private Date updateDt;

    @Column(name = "CREATE_BY")
    private String createBy;

    @Column(name = "UPDATE_BY")
    private String updateBy;
}
