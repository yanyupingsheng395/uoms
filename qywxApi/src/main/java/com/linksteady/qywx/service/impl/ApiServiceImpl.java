package com.linksteady.qywx.service.impl;

import com.linksteady.qywx.dao.ApiMapper;
import com.linksteady.qywx.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ApiServiceImpl implements ApiService {

    @Autowired
    ApiMapper apiMapper;

    @Override
    @Cacheable(value="qywx", key="'qywxDominAddress'")
    public String getQywxDomainAddress() {
        return apiMapper.getQywxDomainAddress();
    }

    @Override
    @Cacheable(value="qywx", key="'qywxDominUrl'")
    public String getQywxDomainUrl() {
        return apiMapper.getQywxDomainUrl();
    }

    @Override
    @Cacheable(value="qywx", key="'qywxCorpId'")
    public String getQywxCorpId() {
        return apiMapper.getQywxCorpId();
    }
}
