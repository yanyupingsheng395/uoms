package com.linksteady.operate.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Reason implements Serializable {

    int reasonId;
    String kpi;

    String reasonName;
    String startDt;
    String endDt;
    String period;

    String status;
    int progress;
    String createDt;
    String updateDt;
    String createBy;
    String updateBy;
    String source;

}
