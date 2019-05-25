package com.linksteady.operate.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.operate.domain.TargetInfo;
import com.linksteady.operate.domain.TgtReference;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

public interface TargetListMapper extends MyMapper<TargetInfo> {
    Long save(@Param("target") TargetInfo target);

    List<Map<String, Object>> getPageList(@Param("startRow") int startRow, @Param("endRow") int endRow, @Param("userId") String userId);

    int getTotalCount();

    Map<String, Object> getDataById(Long id);

    List<TgtReference> getGmvHistoryByPeroid(@Param("sql") String sql);

    TgtReference getGmvHistoryByPeriodDay(@Param("sql") String sql);

    List<TargetInfo> getTargetList();

    Map<String, Object> getMonitorVal(String targetId);

    void updateTargetStatus(@Param("id") long id,@Param("status") String status);
}