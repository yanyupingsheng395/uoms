package com.linksteady.mdss.dao;

import com.linksteady.mdss.config.MyMapper;
import com.linksteady.mdss.domain.DiagDetail;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface DiagDetailMapper extends MyMapper<DiagDetail> {

    void save(@Param("diagDetailList") List<DiagDetail> diagDetailList);

    List<DiagDetail> findByDiagId(@Param("diagId") String diagId);

    void deleteByDiagId(@Param("ids") List<String> ids);

    void updateAlarmFlag(@Param("diagId") String diagId, @Param("nodeId") String nodeId, @Param("flag") String flag);
}