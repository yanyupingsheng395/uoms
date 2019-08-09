package com.linksteady.operate.dao;

import com.linksteady.operate.domain.DailyDetail;
import io.lettuce.core.dynamic.annotation.Param;

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

    List<DailyDetail> getPageList(int start, int end, String headId, @Param("groupIdList") List<String> groupIdList);

    int getDataCount(String headId, @Param("groupIdList") List<String> groupIdList);

    List<DailyDetail> getUserEffect(String headId, int start, int end, String whereInfo);

    int getDataListCount(String headId, String whereInfo);
}
