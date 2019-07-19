package com.linksteady.mdss.dao;

import com.linksteady.mdss.config.MyMapper;
import com.linksteady.mdss.domain.ReasonResult;
import com.linksteady.mdss.domain.ReasonResultTrace;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface ReasonResultMapper extends MyMapper<ReasonResult> {

    List<ReasonResult> getReasonResultList(@Param("reasonId") String reasonId);

    int getReasonResultCount(@Param("reasonId") String reasonId,@Param("reasonCode") String reasonCode);

    void deleteReasonResult(@Param("reasonResultId") String reasonResultId);

    void saveReasonResult(@Param("reasonId") String reasonId,@Param("reasonCode") String reasonCode,@Param("formulaDesc") String formulaDesc,@Param("formula") String formula,@Param("business") String business);

   List<ReasonResultTrace> getReasonResultTraceList(@Param("username") String username);
}