package com.linksteady.operate.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.operate.domain.ReasonResult;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

public interface ReasonResultMapper extends MyMapper<ReasonResult> {

    List<ReasonResult> getReasonResultList(@Param("reasonId") String reasonId);
}