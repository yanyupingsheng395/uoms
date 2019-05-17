package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "UO_DIAG_LIST")
public class Diag {
    /**
     * 主键ID
     */
    @Column(name = "DIAG_ID")
    private Long diagId;

    /**
     * 诊断名称
     */
    @Column(name = "DIAG_NAME")
    private String diagName;

    /**
     * 周期类型 M表示按月 Y表示按天
     */
    @Column(name = "PERIOD_TYPE")
    private String periodType;

    /**
     * 周期开始时间
     */
    @Column(name = "BEGIN_DT")
    private String beginDt;

    /**
     * 周期结束时间
     */
    @Column(name = "END_DT")
    private String endDt;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateDt;

    /**
     * 创建人
     */
    @Column(name = "CREATE_BY")
    private String createBy;

    /**
     * 更新人
     */
    @Column(name = "UPDATE_BY")
    private String updateBy;

    /**
     * 创建人
     */
    @Column(name = "CREATE_BY")
    private String createByName;

    /**
     * 更新人
     */
    @Column(name = "UPDATE_BY")
    private String updateByName;

}