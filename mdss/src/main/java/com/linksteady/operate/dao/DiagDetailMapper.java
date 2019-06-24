package com.linksteady.operate.dao;

import com.linksteady.operate.config.MyMapper;
import com.linksteady.operate.domain.DiagDetail;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface DiagDetailMapper extends MyMapper<DiagDetail> {
    void save(@Param("diagDetailList") List<DiagDetail> diagDetailList);

    List<DiagDetail> findByDiagId(@Param("diagId") String diagId);

    void deleteByDiagId(@Param("ids") List<String> ids);
}