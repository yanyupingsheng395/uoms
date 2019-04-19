package com.linksteady.operate.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.operate.domain.ReasonRelMatrix;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface ReasonRelMatrixMapper extends MyMapper<ReasonRelMatrix> {
    List<ReasonRelMatrix> getMatrix(@Param("code") String code,@Param("reasonId") String reasonId);
    List<ReasonRelMatrix> getMatrixCode(@Param("reasonId") String reasonId);
}