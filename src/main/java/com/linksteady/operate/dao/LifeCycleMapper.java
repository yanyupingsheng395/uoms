package com.linksteady.operate.dao;

import com.linksteady.operate.domain.Diag;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

public interface LifeCycleMapper {

    List<Map<String, Object>> getCatList(@Param("startRow") int startRow, @Param("endRow") int endRow,@Param("orderColumn") String orderColumn,@Param("cateName") String cateName);

    int getCatTotalCount(@Param(value="cateName") String cateName);

}