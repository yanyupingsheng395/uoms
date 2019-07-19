package com.linksteady.mdss.service;

import com.linksteady.mdss.domain.Retention;

import java.util.List;

public interface RetentionService {
    List<Retention> findMonthDataByDate(String startDate, String endDate);
}
