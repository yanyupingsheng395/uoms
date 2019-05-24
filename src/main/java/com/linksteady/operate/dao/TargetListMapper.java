package com.linksteady.operate.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.operate.domain.TargetList;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

public interface TargetListMapper extends MyMapper<TargetList> {
    Long save(@Param("target") TargetList target);
    List<Map<String, Object>> getPageList(@Param("startRow") int startRow, @Param("endRow") int endRow, @Param("userId") String userId);
    int getTotalCount();
    Map<String, Object> getDataById(Long id);
    List<TargetList> getTargetList();
}