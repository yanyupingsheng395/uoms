package com.linksteady.common.domain;

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
    @Column(name = "ID")
    private String id;

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
    private String sortNum;

    @Column(name = "REMARK")
    private String remark;

    @Column(name = "CREATE_DT")
    private Date createDt;
}
