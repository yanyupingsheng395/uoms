package com.linksteady.operate.dao;

import com.linksteady.operate.domain.DailyGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyGroupMapper {

    List<DailyGroup> getDataList(String headId);

    void updateIsChecked(String headId, List<DailyGroup> groupList);

    List<Map<String, Object>> getOriginalGroupCheck();

    int sumCheckedNum(@Param("headId") String headId);
}
