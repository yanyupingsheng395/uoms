package com.linksteady.operate.service;

import com.linksteady.operate.domain.Diag;

import java.util.List;
import java.util.Map;

public interface DiagService{

    List<Diag> getRows(int startRow, int endRow);

    Long getTotalCount();

    Long save(Diag diag);

    List<Map<String, Object>> getNodes(String diagId);
}
