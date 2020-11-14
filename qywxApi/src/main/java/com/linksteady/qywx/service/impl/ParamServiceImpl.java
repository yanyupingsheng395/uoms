package com.linksteady.qywx.service.impl;

import com.linksteady.qywx.dao.ParamMapper;
import com.linksteady.qywx.service.ParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParamServiceImpl implements ParamService {

    @Autowired
    ParamMapper paramMapper;

    @Override
    public String getQywxDomainAddress() {
        return paramMapper.getQywxDomainAddress();
    }

    @Override
    public String getQywxDomainUrl() {
        return paramMapper.getQywxDomainUrl();
    }

    @Override
    public String getQywxCorpId() {
        return paramMapper.getQywxCorpId();
    }


    @Override
    public String getMpAppId() {
        return paramMapper.getMpAppId();
    }

    @Override
    public String getCorpId() {
        return paramMapper.getCorpId();
    }
}
