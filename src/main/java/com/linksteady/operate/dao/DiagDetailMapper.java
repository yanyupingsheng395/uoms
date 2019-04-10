package com.linksteady.operate.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.operate.domain.DiagDetail;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface DiagDetailMapper extends MyMapper<DiagDetail> {
    void save(@Param("diagDetailList") List<DiagDetail> diagDetailList);

    List<DiagDetail> findByDiagId(@Param("diagId") String diagId);

    void deleteByDiagId(@Param("diagId") String diagId);
}