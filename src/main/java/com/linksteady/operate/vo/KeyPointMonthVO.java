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
    int monthId;

    double gmvActual;
    double gmvTarget;
    double gmvCmpRate;
    double getGmvCmpTotalRate;
    double timeRate;
    double gmvLastMonth;
    double gmvLastMonthDiff;
    double gmvLastMonthRate;
    double gmvPreMonth;
    double gmvPreMonthDiff;
    double gmvPreMonthRate;

    double costMonth;
    double incomeMonth;
    double profitRate;
    double profitAlertRate;
    String  costHealthFlag;

}
