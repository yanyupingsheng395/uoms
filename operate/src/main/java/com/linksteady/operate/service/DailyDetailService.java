package com.linksteady.operate.service;

import com.linksteady.operate.domain.DailyDetail;
import com.linksteady.operate.vo.Echart;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyDetailService {

    Echart getTargetType(String headId);

    Echart getUrgency(String headId);

    List<DailyDetail> getPageList(int start, int end, String headId, String groupIds, String pathActive, String sortColumn, String sortOrder);

    int getDataCount(String headId, String groupIds, String pathActive);

    List<DailyDetail> getUserEffect(String headId, int start, int end, String userValue, String pathActive, String status);

    int getDataListCount(String headId, String userValue, String pathActive, String status);
}
