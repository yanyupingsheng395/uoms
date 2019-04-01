package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.DiagMapper;
import com.linksteady.operate.domain.Diag;
import com.linksteady.operate.service.DiagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiagServiceImpl implements DiagService {

    @Autowired
    private DiagMapper diagMapper;

    @Override
    public List<Diag> getRows(int startRow, int endRow) {
        return diagMapper.getList(startRow, endRow);
    }

    @Override
    public Long getTotalCount() {
        return diagMapper.getTotalCount();
    }
}
