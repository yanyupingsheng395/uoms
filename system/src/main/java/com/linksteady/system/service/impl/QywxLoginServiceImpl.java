package com.linksteady.system.service.impl;

import com.linksteady.system.dao.QywxLoginMapper;
import com.linksteady.system.service.QywxLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QywxLoginServiceImpl implements QywxLoginService {

    @Autowired
    QywxLoginMapper qywxLoginMapper;

    @Override
    public String  getCorpId() {
        return qywxLoginMapper.getCorpId();
    }

    @Override
    public String getAgentId() {
        return qywxLoginMapper.getAgentId();
    }
}
