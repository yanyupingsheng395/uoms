package com.linksteady.operate.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 关键指标年度执行情况
 * @author  huang
 */
@Data
@ToString
public class KeyPointYearVO implements Serializable {

    Integer yearId;

    Double gmvActual;
    Double gmvTarget;
    Double gmvCmpRate;
    Double getGmvCmpTotalRate;
    Double timeRate;

    Double gmvPreYear;
    Double gmvPreYearDiff;
    Double gmvPreYearRate;

    Double costYear;
    Double incomeYear;
    Double profitRate;
    Double profitAlertRate;
    String  costHealthFlag;
    Double volatility;
    Integer reachNum;
    Integer unreachNum;
    Integer ongoingNum;
    Integer nostartNum;

}
