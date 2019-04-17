package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.linksteady.operate.dao.DiagConditionMapper;
import com.linksteady.operate.dao.DiagDetailMapper;
import com.linksteady.operate.domain.DiagCondition;
import com.linksteady.operate.domain.DiagDetail;
import com.linksteady.operate.service.DiagDetailService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class DiagDetailServiceImpl implements DiagDetailService {

    @Autowired
    private DiagDetailMapper diagDetailMapper;

    @Autowired
    private DiagConditionMapper diagConditionMapper;

    @Override
    @Transactional
    public void save(String data) {
        List<DiagDetail> diagDetailList = new ArrayList<>();
        List<DiagCondition> conditions = new ArrayList<>();
        JSONArray jsonArray = JSON.parseArray(data);
        Iterator it = jsonArray.iterator();
        while(it.hasNext()) {
            JSONObject jsonObject = (JSONObject)it.next();
            DiagDetail diagDetail = jsonObject.toJavaObject(DiagDetail.class);
            diagDetailList.add(diagDetail);

            JSONArray conditionJsonArray = jsonObject.getJSONArray("conditions");
            if(conditionJsonArray != null) {
                Iterator itConditions = conditionJsonArray.iterator();
                while(itConditions.hasNext()) {
                    String tmp = (String) itConditions.next();
                    String[] tmpArray = tmp.split(":");
                    DiagCondition condition = new DiagCondition();
                    condition.setDiagId(diagDetail.getDiagId());
                    condition.setDimValues(tmpArray[0]);
                    condition.setDimCode(tmpArray[1]);
                    condition.setDimName(tmpArray[2]);
                    condition.setDimValueDisplay(tmpArray[3]);
                    condition.setInheritFlag(tmpArray[4]);
                    condition.setNodeId(diagDetail.getNodeId());
                    condition.setCreateDt(new Date());
                    conditions.add(condition);

                }
            }
        }
        diagDetailMapper.save(diagDetailList);
        // 保存条件
        if(conditions.size() != 0) {
            diagConditionMapper.save(conditions);
        }
    }
}
