package com.linksteady.operate.service;

import com.linksteady.operate.domain.DailyExecute;
import com.linksteady.operate.vo.Echart;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-08-06
 */
public interface DailyExecuteService {

    Map<String, Echart> getKpiTrend(String headId);
}
