package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.dao.DiagConditionMapper;
import com.linksteady.operate.dao.DiagDetailMapper;
import com.linksteady.operate.dao.DiagMapper;
import com.linksteady.operate.domain.Diag;
import com.linksteady.operate.domain.DiagCondition;
import com.linksteady.operate.domain.DiagDetail;
import com.linksteady.operate.service.DiagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DiagServiceImpl implements DiagService {

    @Autowired
    private DiagMapper diagMapper;

    @Autowired
    private DiagDetailMapper diagDetailMapper;

    @Autowired
    private DiagConditionMapper diagConditionMapper;

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
    public List<Map<String, Object>> getNodes(String diagId) {
        List<Map<String, Object>> resultList = new ArrayList<>();
//        List<Diag> diagList = diagMapper.findByDiagId(diagId);
        List<DiagDetail> diagDetailList = diagDetailMapper.findByDiagId(diagId);
//        List<DiagCondition> diagConditions = diagConditionMapper.findByDiagId(diagId);
        for(DiagDetail d:diagDetailList) {
            Map<String, Object> result = new HashMap<>();
            result.put("id", d.getNodeId());
            result.put("parentid", d.getParentId());
            if (d.getParentId() == null) {
                result.put("isroot", true);
            }else {
                result.put("isroot", false);
            }
            result.put("topic", d.getNodeName());
            resultList.add(result);
        }
        return resultList;
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        diagMapper.deleteById(id);
        diagDetailMapper.deleteByDiagId(id);
        diagConditionMapper.deleteByDiagId(id);
    }
}
