package com.linksteady.operate.service;

import com.linksteady.operate.domain.DailyInfo;
import com.linksteady.operate.vo.Echart;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyService {

    List<DailyInfo> getPageList(int start, int end, String touchDt);

    List<DailyInfo> getTouchPageList(int start, int end, String touchDt);

    int getTotalCount(String touchDt);

    int getTouchTotalCount(String touchDt);

    Map<String, Object> getTipInfo(String headId);

    void updateStatus(String headId, String status);

    void updateCheckNum(String headId, List<String> groupIds);
}
