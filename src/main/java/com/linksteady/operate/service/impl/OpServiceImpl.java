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

    @Override
    public Integer getOpDayUserCountInfo(String daywid) {
        return opMapper.getOpDayUserCountInfo(daywid);
    }

    @Override
    public List<Map<String, Object>> getOpDayDetailList(int startRow, int endRow, String daywid) {
        return opMapper.getOpDayDetailList(startRow,endRow,daywid);
    }

    @Override
    public int getOpDayDetailListCount(String daywid) {
        return opMapper.getOpDayDetailListCount(daywid);
    }

    @Override
    public List<Map<String, Object>> getOpDayDetailAllList(String daywid) {
        return opMapper.getOpDayDetailAllList(daywid);
    }

    @Override
    public List<Map<String, Object>> getPeriodHeaderList(int startRow, int endRow) {
        return opMapper.getPeriodHeaderList(startRow,endRow);
    }

    @Override
    public int getPeriodListCount() {
        return opMapper.getPeriodListCount();
    }

    @Override
    public void savePeriodHeaderInfo(String periodName, String startDt, String endDt) {
        opMapper.savePeriodHeaderInfo(periodName,startDt,endDt);
    }

    @Override
    public List<Map<String, Object>> getPeriodUserList(int startRow, int endRow, String headerId) {
        return opMapper.getPeriodUserList(startRow, endRow,headerId);
    }

    @Override
    public int getPeriodUserListCount(String headerId) {
        return opMapper.getPeriodUserListCount(headerId);
    }
}
