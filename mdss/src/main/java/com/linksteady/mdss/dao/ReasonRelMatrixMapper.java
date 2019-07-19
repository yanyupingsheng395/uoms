package com.linksteady.mdss.dao;

import com.linksteady.mdss.config.MyMapper;
import com.linksteady.mdss.domain.ReasonRelMatrix;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface ReasonRelMatrixMapper extends MyMapper<ReasonRelMatrix> {
    List<ReasonRelMatrix> getMatrix(@Param("code") String code,@Param("reasonId") String reasonId);
    List<ReasonRelMatrix> getMatrixCode(@Param("reasonId") String reasonId);

    List<ReasonRelMatrix>  getReasonRelateInfoByCode(@Param("reasonId") String reasonId, @Param("fcode") String fcode, @Param("rfcode") String rfcode);
}