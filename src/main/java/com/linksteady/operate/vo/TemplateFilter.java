package com.linksteady.operate.vo;

import lombok.Data;

@Data
public class TemplateFilter {

    String dimCode;

    String dimValues;

    public TemplateFilter(String dimCode, String dimValues) {
        this.dimCode = dimCode;
        this.dimValues = dimValues;
    }
}
