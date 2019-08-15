package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2019-08-14
 */
@Data
public class ActivityWeight {

    private Long headId;

    /**
     * 权重指数
     */
    private Double weightIdx;

    /**
     * 活动日期
     */
    private Date activDt;
}
