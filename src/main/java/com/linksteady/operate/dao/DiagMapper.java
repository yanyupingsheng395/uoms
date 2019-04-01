package com.linksteady.operate.dao;

import com.linksteady.operate.domain.Diag;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;

public interface DiagMapper{

    List<Diag> getList(@Param("startRow") int startRow, @Param("endRow") int endRow);

    Long getTotalCount();
}