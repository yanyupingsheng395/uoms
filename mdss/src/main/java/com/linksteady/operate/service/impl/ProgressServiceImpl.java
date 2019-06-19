package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.ProgressMapper;
import com.linksteady.operate.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProgressServiceImpl implements ProgressService {

    @Autowired
    private ProgressMapper progressMapper;

    @Override
    public Long getNodeIdFromSequence() {
        return progressMapper.getNodeIdFromSequence();
    }
}
