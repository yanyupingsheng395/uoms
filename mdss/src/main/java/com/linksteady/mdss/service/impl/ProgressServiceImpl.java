package com.linksteady.mdss.service.impl;

import com.linksteady.mdss.dao.ProgressMapper;
import com.linksteady.mdss.service.ProgressService;
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
