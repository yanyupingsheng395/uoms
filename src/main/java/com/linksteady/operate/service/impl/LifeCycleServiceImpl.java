package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.LifeCycleMapper;
import com.linksteady.operate.domain.LcSpuInfo;
import com.linksteady.operate.service.LifeCycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LifeCycleServiceImpl implements LifeCycleService {

    @Autowired
    private LifeCycleMapper lifeCycleMapper;


    @Override
    public List<LcSpuInfo> getSpuList() {
        return lifeCycleMapper.getSpuList();
    }

}
