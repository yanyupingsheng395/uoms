package com.linksteady.operate.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 *月度关键指标完成情况的VO
 */
@Data
@ToString
public class KeyPointMonthVO  implements Serializable {
    Integer monthId;

    Double gmvActual;
    Double gmvTarget;
    Double gmvCmpRate;
    Double getGmvCmpTotalRate;
    Double timeRate;
    Double gmvLastMonth;
    Double gmvLastMonthDiff;
    Double gmvLastMonthRate;
    Double gmvPreMonth;
    Double gmvPreMonthDiff;
    Double gmvPreMonthRate;

    Double costMonth;
    Double incomeMonth;
    Double profitRate;
    Double profitAlertRate;
    String  costHealthFlag;

}
