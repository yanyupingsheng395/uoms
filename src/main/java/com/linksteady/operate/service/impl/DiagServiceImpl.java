package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.DiagMapper;
import com.linksteady.operate.domain.Diag;
import com.linksteady.operate.service.DiagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    @Transactional
    public Long save(Diag diag) {
        diagMapper.save(diag);
        return diag.getDiagId();
    }

    @Override
    public Map<String, Object> getNodes(String diagId) {
        Map<String, Object> result = new HashMap<>();
        List<Diag> diagList = diagMapper.findByDiagId(diagId);
//        for(Diag d:diagList) {
////            result.put("id", d.get);
//        }
        return null;
    }
}
