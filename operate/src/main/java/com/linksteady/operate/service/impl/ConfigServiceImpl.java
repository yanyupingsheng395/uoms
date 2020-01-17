package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.ConfigMapper;
import com.linksteady.operate.dao.CouponMapper;
import com.linksteady.operate.dao.DailyMapper;
import com.linksteady.operate.domain.CouponInfo;
import com.linksteady.operate.service.ConfigService;
import com.linksteady.operate.service.CouPonService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by hxcao on 2019-04-29
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private ConfigMapper configMapper;

    @Override
    public List<Map<String, String>> selectPathActive() {
        return configMapper.selectPathActive();
    }

    @Override
    public List<Map<String, String>> selectUserValue() {
        return configMapper.selectUserValue();
    }

    @Override
    public List<Map<String, String>> selectLifeCycle() {
        return configMapper.selectLifeCycle();
    }

    @Override
    public List<Map<String, String>> selectCommonConfig() {
        return configMapper.selectCommonConfig();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePathActive(String active) {
        if(StringUtils.isNotEmpty(active)) {
            configMapper.updatePathActive(active);
        }
    }
}
