package com.linksteady.operate.domain;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class AddUserEffect {
    private Long effectId;
    private Long headId;
    private Long scheduleId;
    private Date applyDate;
    private long applyNum;

    private LocalDate statisDate;
    private long statisDay;
    private long statisPassNum;
    private double statisPassRate;
}
