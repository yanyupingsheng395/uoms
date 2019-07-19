package com.linksteady.mdss.dao;

import com.linksteady.mdss.config.MyMapper;
import com.linksteady.mdss.domain.TargetDimension;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

public interface TargetDimensionMapper extends MyMapper<TargetDimension> {

    void save(@Param("dimensionList") List<TargetDimension> dimensionList);

    Long getIdFromDual();

    List<Map<String, Object>> getListByTgtId(Long id);

    void deleteTgtDimensionById(@Param("ids") List<String> ids);
}