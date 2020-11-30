package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author HUANG
 * @date 2019-10-25
 */
@Data
public class QywxDailyStatis {

    private Long headId;

    private Double convertRate;

    private Long touchDate;

    private Long convertNum;

    private Long convertSpuNum;

    private Double convertSpuRate;

    private Date insertDt;

    private Date conversionDate;

    private String conversionDateStr;
}
