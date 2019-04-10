package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.LifeCycleMapper;
import com.linksteady.operate.dao.OpMapper;
import com.linksteady.operate.service.LifeCycleService;
import com.linksteady.operate.service.OpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OpServiceImpl implements OpService {

    @Autowired
    private OpMapper opMapper;


    @Override
    public List<Map<String, Object>> getOpDayList(int startRow, int endRow,String daywid) {
        return opMapper.getOpDayList(startRow, endRow,daywid);
    }

    @Override
    public int getOpDayListCount(String daywid) {
        return opMapper.getOpDayListCount(daywid);
    }
}
