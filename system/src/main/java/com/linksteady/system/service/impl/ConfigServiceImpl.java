package com.linksteady.system.service.impl;

import com.linksteady.system.dao.ConfigMapper;
import com.linksteady.system.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by admin
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private ConfigMapper configMapper;

    @Override
    public List<Map<String, String>> selectCommonConfig() {
        return configMapper.selectCommonConfig();
    }

}
