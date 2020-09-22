package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Transient;
import java.util.Date;

/**
 * @author hxcao
 * @date 2019-11-04
 */
@Data
public class ActivityTemplate {

    private Long code;

    private String name;

    private String content;

    private String remark;

    private String insertBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date insertDt;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date updateDt;

    /**
     * 个性化
     */
    private String isPersonal;

    /**
     * 使用场景
     */
    private String scene;

    private String isProdUrl;

    private String isProdName;

    private String isPrice;

    private String isProfit;

    /**
     * 是否当前活动正在使用的文案
     */
    @Transient
    private String isCurrent;

    @Transient
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date opDt;

    private Integer usedDays;
}
