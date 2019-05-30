package com.linksteady.operate.service;

import com.linksteady.operate.domain.Diag;
import com.linksteady.operate.domain.DiagCondition;
import com.linksteady.operate.vo.NodeDataVO;

import java.util.List;
import java.util.Map;

public interface DiagService{

    List<Diag> getRows(int startRow, int endRow);

    Long getTotalCount();

    Long save(Diag diag, List<DiagCondition> conditionList);

    List<NodeDataVO> getNodes(String diagId);

    void deleteById(String id);

    Map<String, Object> geDiagInfoById(String diagId);
}
