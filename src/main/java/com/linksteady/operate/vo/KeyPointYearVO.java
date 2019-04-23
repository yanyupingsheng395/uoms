package com.linksteady.operate.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class KeyPointYearVO implements Serializable {

    int yearId;

    double gmvActual;
    double gmvTarget;
    double gmvCmpRate;
    double getGmvCmpTotalRate;
    double timeRate;

    double gmvPreYear;
    double gmvPreYearDiff;
    double gmvPreYearRate;

    double costYear;
    double incomeYear;
    double profitRate;
    double profitAlertRate;
    String  costHealthFlag;
    double volatility;
    int reachNum;
    int unreachNum;
    int ongoingNum;
    int nostartNum;

}
