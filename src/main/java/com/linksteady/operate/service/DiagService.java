package com.linksteady.operate.service;

import com.linksteady.operate.domain.Diag;

import java.util.List;

public interface DiagService{

    List<Diag> getRows(int startRow, int endRow);

    Long getTotalCount();

    void save(Diag diag);
}
