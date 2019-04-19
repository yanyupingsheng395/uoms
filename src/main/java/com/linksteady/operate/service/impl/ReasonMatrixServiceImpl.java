package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.ReasonRelMatrixMapper;
import com.linksteady.operate.domain.ReasonRelMatrix;
import com.linksteady.operate.service.ReasonMatrixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReasonMatrixServiceImpl implements ReasonMatrixService {

    @Autowired
    private ReasonRelMatrixMapper reasonRelMatrixMapper;

    @Override
    public Map<String, Object> getMatrix(String reasonId) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<ReasonRelMatrix> codeList = reasonRelMatrixMapper.getMatrixCode(reasonId);
        for(ReasonRelMatrix r:codeList) {
            List<ReasonRelMatrix> dataList = reasonRelMatrixMapper.getMatrix(r.getfCode(), reasonId);
            result.put(r.getfCode(), dataList);
        }
        return result;
    }
}
