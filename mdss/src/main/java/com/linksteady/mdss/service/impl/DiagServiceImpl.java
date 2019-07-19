package com.linksteady.mdss.service.impl;

import com.linksteady.mdss.dao.DiagConditionMapper;
import com.linksteady.mdss.dao.DiagDetailMapper;
import com.linksteady.mdss.dao.DiagMapper;
import com.linksteady.mdss.domain.Diag;
import com.linksteady.mdss.domain.DiagCondition;
import com.linksteady.mdss.domain.DiagDetail;
import com.linksteady.mdss.service.DiagService;
import com.linksteady.mdss.vo.NodeDataVO;
import com.linksteady.common.domain.User;
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

    /**
     * 获取诊断数据行
     * @param startRow
     * @param endRow
     * @return
     */
    @Override
    public List<Diag> getRows(int startRow, int endRow, String diagName) {
        String username = ((User)SecurityUtils.getSubject().getPrincipal()).getUsername();
        return diagMapper.getList(startRow, endRow, username, diagName);
    }

    /**
     * 获取当前登录账号总的诊断数据行
     * @return
     */
    @Override
    public Long getTotalCount(String diagName) {
        String username = ((User)SecurityUtils.getSubject().getPrincipal()).getUsername();
        return diagMapper.getTotalCount(username, diagName);
    }

    /**
     * 保存诊断头数据
     * @param diag
     * @param diagConditions
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(Diag diag, List<DiagCondition> diagConditions) {
        String username = ((User)SecurityUtils.getSubject().getPrincipal()).getUsername();
        diag.setCreateBy(username);
        diag.setCreateDt(new Date());
        diag.setUpdateDt(new Date());
        diagMapper.save(diag);
        Long diagId = diag.getDiagId();
        return diagId;
    }

    /**
     *  查看，编辑获取diagId关联的所有节点信息
     * @param diagId
     * @return
     */
    @Override
    public List<NodeDataVO> getNodes(String diagId) {
        List<NodeDataVO> resultList = new ArrayList<>();
        List<DiagDetail> diagDetailList = diagDetailMapper.findByDiagId(diagId);
        for(DiagDetail d:diagDetailList) {
            NodeDataVO node = new NodeDataVO();
            node.setId(String.valueOf(d.getNodeId()));
            node.setParentid(String.valueOf(d.getParentId()));
            if (d.getParentId() == null) {
                node.setIsroot(true);
            }else {
                node.setIsroot(false);
            }
            node.setTopic(d.getNodeName());
            List<DiagCondition> diagConditions = diagConditionMapper.findByDiagIdAndNodeId(diagId, String.valueOf(d.getNodeId()));
            node.setKpiCode(d.getKpiCode());
            node.setKpiName(d.getKpiName());
            node.setAlarmFlag(d.getAlarmFlag());
            node.setKpiLevelId(String.valueOf(d.getKpiLevelId()));
            node.setConditions(diagConditions);
            resultList.add(node);
        }
        return resultList;
    }

    /**
     * 删除诊断头数据
     * @param id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String id) {
        List<String> ids = Arrays.asList(id.split(","));
        diagMapper.deleteById(ids);
        diagDetailMapper.deleteByDiagId(ids);
        diagConditionMapper.deleteByDiagId(ids);
    }

    /**
     * 通过ID获取诊断数据信息
     * @param diagId
     * @return
     */
    @Override
    public Map<String, Object> geDiagInfoById(String diagId) {
        return diagMapper.geDiagInfoById(diagId);
    }
}
