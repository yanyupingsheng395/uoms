package com.linksteady.mdss.domain;

import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "UO_KPI_SQLTEMPLATE")
public class KpiSqlTemplate {
    @Column(name = "SQL_TEMPLATE_CODE")
    private String sqlTemplateCode;

    @Column(name = "SQL_TEMPLATE")
    private String sqlTemplate;

    @Column(name = "COMMENTS")
    private String comments;

    @Column(name = "RESULT_TYPE")
    private String resultType;

    @Column(name = "RESULT_MAPPING")
    private String resultMapping;

    @Column(name = "DRIVER_TABLES")
    private String driverTables;
}