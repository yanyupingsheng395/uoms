package com.linksteady.mdss.dao;

import com.linksteady.mdss.config.MyMapper;
import com.linksteady.mdss.domain.DiagCondition;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface DiagConditionMapper extends MyMapper<DiagCondition> {

    void save(@RequestParam("conditions") List<DiagCondition> conditions);

    List<DiagCondition> findByDiagId(@Param("diagId") String diagId);

    List<DiagCondition> findByDiagIdAndNodeId(@Param("diagId") String diagId, @Param("nodeId") String nodeId);

    void deleteByDiagId(@Param("ids") List<String> ids);
}