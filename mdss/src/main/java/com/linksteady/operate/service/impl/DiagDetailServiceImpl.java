package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.linksteady.operate.dao.DiagConditionMapper;
import com.linksteady.operate.dao.DiagDetailMapper;
import com.linksteady.operate.domain.DiagCondition;
import com.linksteady.operate.domain.DiagDetail;
import com.linksteady.operate.service.DiagDetailService;
import com.linksteady.operate.vo.DiagConditionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DiagDetailServiceImpl implements DiagDetailService {

    @Autowired
    private DiagDetailMapper diagDetailMapper;

    @Autowired
    private DiagConditionMapper diagConditionMapper;

    /**
     * 保存节点详细信息和节点的条件信息
     * @param data
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(String data) {
        List<DiagDetail> diagDetailList = new ArrayList<>();
        List<DiagCondition> conditions = new ArrayList<>();
        JSONArray jsonArray = JSON.parseArray(data);
        Iterator it = jsonArray.iterator();
        while(it.hasNext()) {
            JSONObject jsonObject = (JSONObject)it.next();
            DiagDetail diagDetail = jsonObject.toJavaObject(DiagDetail.class);
            diagDetailList.add(diagDetail);
            if(null != diagDetail.getCondition()) {
                for (DiagCondition d:diagDetail.getCondition()
                ) {
                    d.setDiagId(diagDetail.getDiagId());
                    d.setNodeId(diagDetail.getNodeId());
                    d.setCreateDt(new Date());
                    conditions.add(d);
                }
            }
        }
        diagDetailMapper.save(diagDetailList);
        // 保存条件
        if(conditions.size() != 0) {
            /**
             * 过滤重复条件
             */
            conditions = conditions.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()->new TreeSet<>(Comparator.comparing(DiagCondition::getDimCode).thenComparing(DiagCondition::getDimValues))), ArrayList::new));
            diagConditionMapper.save(conditions);
        }
    }
}
