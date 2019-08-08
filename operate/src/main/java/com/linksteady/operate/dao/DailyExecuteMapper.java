package com.linksteady.operate.dao;

import com.linksteady.operate.domain.DailyExecute;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-08-06
 */
public interface DailyExecuteMapper {

    List<DailyExecute> getKpiTrend(String headId);
}
