package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.DiagConditionMapper;
import com.linksteady.operate.dao.DiagDetailMapper;
import com.linksteady.operate.dao.DiagMapper;
import com.linksteady.operate.domain.Diag;
import com.linksteady.operate.domain.DiagCondition;
import com.linksteady.operate.domain.DiagDetail;
import com.linksteady.operate.service.DiagService;
import com.linksteady.system.domain.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    public Long save(Diag diag, List<DiagCondition> diagConditions) {
        String username = ((User)SecurityUtils.getSubject().getPrincipal()).getUsername();
        diag.setCreateBy(username);
        diag.setCreateDt(new Date());
        diag.setUpdateDt(new Date());
        diagMapper.save(diag);
        Long diagId = diag.getDiagId();
        if(diagConditions.size() > 0) {
            diagConditions.stream().forEach(x-> {
                x.setNodeId(-1L);
                x.setDiagId(diagId);
                x.setInheritFlag("n");
                x.setCreateDt(new Date());
            });
            diagConditionMapper.save(diagConditions);
        }
        return diagId;
    }

    @Override
    public List<Map<String, Object>> getNodes(String diagId) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<DiagDetail> diagDetailList = diagDetailMapper.findByDiagId(diagId);

        for(DiagDetail d:diagDetailList) {
            Map<String, Object> result = new HashMap<>();
            result.put("id", d.getNodeId());
            result.put("parentid", d.getParentId());
            result.put("KPI_LEVEL_ID", d.getKpiLevelId());
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

    @Override
    public String getDimByDiagId(String diagId) {
        return diagMapper.getDimByDiagId(diagId);
    }
}
