package com.linksteady.operate.service;

import com.linksteady.operate.domain.Retention;

import java.util.List;

public interface RetentionService {
    List<Retention> findMonthDataByDate(String startDate, String endDate);
}
