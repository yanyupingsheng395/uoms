package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2019-10-25
 */
@Data
public class DailyStatis {

    private Long headId;

    private Double convertRate;

    private Date touchDate;

    private Long convertNum;

    private Long convertSpuNum;

    private Double convertSpuRate;

    private Date insertDt;

    private String touchDateStr;
}
