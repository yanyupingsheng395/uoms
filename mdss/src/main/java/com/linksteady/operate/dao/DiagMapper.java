package com.linksteady.operate.dao;

import com.linksteady.operate.domain.Diag;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Map;

public interface DiagMapper{

    List<Diag> getList(@Param("startRow") int startRow, @Param("endRow") int endRow, @Param("username") String username);

    Long getTotalCount(@Param("username") String username);

    void save(@Param("diag") Diag diag);

    List<Diag> findByDiagId(@Param("diag") String diagId);

    void deleteById(@Param("id") String id);

    Map<String, Object> geDiagInfoById(@Param("diagId") String diagId);
}