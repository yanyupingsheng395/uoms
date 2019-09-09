package com.linksteady.operate.service;

import com.linksteady.operate.vo.Echart;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-08-14
 */
public interface ActivityWeightService {

    Echart getWeightIdx(String startDt, String endDt, String dateRange);

    /**
     * 对于大促活动，获取其影响日期
     */
    Map<String,String> getEffectDate(String beginDate,String endDate);

}
