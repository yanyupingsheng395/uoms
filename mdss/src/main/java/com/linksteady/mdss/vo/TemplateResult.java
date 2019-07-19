package com.linksteady.mdss.vo;

import lombok.Data;

@Data
public class TemplateResult {

    /**
     * join
     */
    String joinInfo;

    /**
     * where
     */
    String filterInfo;

    /**
     * from
     */
    String fromInfo;

    /**
     * 查询字段
     */
    String fieldInfo;
}
