package com.linksteady.operate.vo;

import lombok.Data;

/**
 * 诊断- 过滤条件
 * @author  huang
 */
@Data
public class TemplateFilter {

    String dimCode;

    String dimValues;

    public TemplateFilter(String dimCode, String dimValues) {
        this.dimCode = dimCode;
        this.dimValues = dimValues;
    }
}
