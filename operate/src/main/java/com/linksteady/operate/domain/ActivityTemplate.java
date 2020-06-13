package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2019-11-04
 */
@Data
public class ActivityTemplate {

    private String code;

    private String name;

    private String content;

    private String remark;

    private String insertBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date insertDt;

    // 个性化
    private String isPersonal;

    // 用户与商品关系
    private String relation;

    // 使用场景
    private String scene;

    private String isProdUrl;

    private String isProdName;

    private String isPrice;

    private String isProfit;
}
