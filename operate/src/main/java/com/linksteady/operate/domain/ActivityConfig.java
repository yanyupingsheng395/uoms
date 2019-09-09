package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2019-09-06
 */
@Data
public class ActivityConfig {

    private Long id;

    private String activityName;

    private String activityType;

    private String startDate;

    private String endDate;

    private String beforeDate;

    private String afterDate;
}
