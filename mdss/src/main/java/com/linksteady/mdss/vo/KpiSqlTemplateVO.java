package com.linksteady.mdss.vo;

import lombok.Data;

import java.util.Map;

/**
 * SQL模板
 * @author  huang
 */
@Data
public class KpiSqlTemplateVO {

    private String sqlTemplateCode;

    private String sqlTemplate;

    private String comments;

    private String resultType;

    private String resultMapping;

    private Map<String,String> driverTableMapping;
}
