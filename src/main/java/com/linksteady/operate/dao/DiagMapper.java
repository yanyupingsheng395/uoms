package com.linksteady.operate.dao;

import com.linksteady.operate.domain.Diag;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;

public interface DiagMapper{

    List<Diag> getList(@Param("startRow") int startRow, @Param("endRow") int endRow);

    Long getTotalCount();

    void save(@Param("diag") Diag diag);

    List<Diag> findByDiagId(@Param("diag") String diagId);

    void deleteById(@Param("id") String id);

    String getDimByDiagId(@Param("diagId") String diagId);
}