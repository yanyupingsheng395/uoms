package com.linksteady.operate.service;

import com.linksteady.operate.vo.Echart;

/**
 * @author hxcao
 * @date 2019-08-13
 */
public interface ActivityDetailService {

    Echart getUserCountData(String startDt, String endDt, String dateRange);
}
