package com.linksteady.operate.domain;

import lombok.Data;

/**
 * Created by hxcao on 2019-05-22
 */
@Data
public class TgtReference {

    private String period;

    private String kpi;

    private String yearOnYear;

    private String yearOverYear;

    public TgtReference() {

    }

    public TgtReference(String period, String kpi, String yearOnYear, String yearOverYear) {
        this.period = period;
        this.kpi = kpi;
        this.yearOnYear = yearOnYear;
        this.yearOverYear = yearOverYear;
    }
}
