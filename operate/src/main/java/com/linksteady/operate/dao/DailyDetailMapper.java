package com.linksteady.operate.dao;

import com.linksteady.operate.domain.DailyDetail;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyDetailMapper {

    List<Map<String, Object>> getTargetType(String headId);

    List<Map<String, Object>> getTargetTypeOfCheck(String headId);

    List<Map<String, Object>> getUrgency(String headId);

    List<Map<String, Object>> getUrgencyOfCheck(String headId);

    List<DailyDetail> getPageList(int start, int end, String headId, String groupId);

    int getDataCount(String headId, String groupId);
}
