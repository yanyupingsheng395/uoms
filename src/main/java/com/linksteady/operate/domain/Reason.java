package com.linksteady.operate.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author system
 */
@Data
public class Reason implements Serializable {

    int rn;
    int reasonId;
    String kpiCode;
    String kpiName;

    String reasonName;
    String beginDt;
    String endDt;
    String periodType;
    String periodName;

    String status;
    int progress;
    String createDt;
    String updateDt;
    String createBy;
    String updateBy;
    String source;

}
