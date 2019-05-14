package com.linksteady.operate.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.operate.domain.ReasonResult;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

public interface ReasonResultMapper extends MyMapper<ReasonResult> {

    List<ReasonResult> getReasonResultList(@Param("reasonId") String reasonId);

    int getReasonResultCount(@Param("reasonId") String reasonId,@Param("reasonCode") String reasonCode);

    void deleteReasonResult(@Param("reasonId") String reasonId,@Param("reasonCode") String reasonCode);

    void saveReasonResult(@Param("reasonId") String reasonId,@Param("reasonCode") String reasonCode,@Param("formulaDesc") String formulaDesc,@Param("formula") String formula,@Param("business") String business);
}