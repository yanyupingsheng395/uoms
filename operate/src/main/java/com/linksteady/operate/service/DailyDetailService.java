package com.linksteady.operate.service;

import com.linksteady.operate.domain.DailyDetail;
import com.linksteady.operate.vo.Echart;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyDetailService {

    List<DailyDetail> getPageList(int start, int end, String headId, String userValue, String pathActive);

    int getDataCount(String headId, String userValue, String pathActive);

    List<DailyDetail> getStrategyPageList(int start, int end, String headId);

    int getStrategyCount(String headId);

    List<DailyDetail> getUserEffect(String headId, int start, int end, String userValue, String pathActive, String status);

    int getDataListCount(String headId, String userValue, String pathActive, String status);
}
