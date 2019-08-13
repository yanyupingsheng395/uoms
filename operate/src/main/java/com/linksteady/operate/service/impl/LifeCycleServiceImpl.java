package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.LifeCycleMapper;
import com.linksteady.operate.domain.LcSpuInfo;
import com.linksteady.operate.service.LifeCycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LifeCycleServiceImpl implements LifeCycleService {

    @Autowired
    private LifeCycleMapper lifeCycleMapper;


    @Override
    public List<LcSpuInfo> getSpuList(String startDt, String endDt) {
        return lifeCycleMapper.getSpuList(startDt,endDt);
    }

    @Override
    public List<LcSpuInfo> getSpuListWithUserCount(String startDt, String endDt) {
        return lifeCycleMapper.getSpuListWithUserCount(startDt,endDt);
    }

    @Override
    public List<LcSpuInfo> getSpuListWithPoCount(String startDt, String endDt) {
        return lifeCycleMapper.getSpuListWithPoCount(startDt,endDt);
    }

    @Override
    public List<LcSpuInfo> getSpuListWithJoinRate(String startDt, String endDt) {
        return lifeCycleMapper.getSpuListWithJoinRate(startDt,endDt);
    }

    @Override
    public List<LcSpuInfo> getSpuListWithSprice(String startDt, String endDt) {
        return lifeCycleMapper.getSpuListWithSprice(startDt,endDt);
    }
}
