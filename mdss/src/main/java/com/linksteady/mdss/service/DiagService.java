package com.linksteady.mdss.service;

import com.linksteady.mdss.domain.Diag;
import com.linksteady.mdss.domain.DiagCondition;
import com.linksteady.mdss.vo.NodeDataVO;

import java.util.List;
import java.util.Map;

public interface DiagService{

    List<Diag> getRows(int startRow, int endRow, String diagName);

    Long getTotalCount(String diagName);

    Long save(Diag diag, List<DiagCondition> conditionList);

    List<NodeDataVO> getNodes(String diagId);

    void deleteById(String id);

    Map<String, Object> geDiagInfoById(String diagId);
}
