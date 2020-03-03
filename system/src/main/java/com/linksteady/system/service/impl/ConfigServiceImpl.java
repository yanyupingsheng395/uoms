package com.linksteady.system.service.impl;

import com.linksteady.common.domain.Tconfig;
import com.linksteady.system.dao.ConfigMapper;
import com.linksteady.system.service.ConfigService;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
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

    private static final String CONFIG_KEY_NAME="TCONFIG";

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public synchronized  void loadConfigToRedis() {
        List<Tconfig> tconfigList=configMapper.selectCommonConfig();
        HashOperations<String, String, Tconfig> hashOperations= redisTemplate.opsForHash();

        //删除
        redisTemplate.delete(CONFIG_KEY_NAME);
        //存入redis
        for(Tconfig tconfig:tconfigList)
        {
            hashOperations.put(CONFIG_KEY_NAME,tconfig.getName(),tconfig);
        }
    }

    @Override
    public String getValueByName(String name) {
        HashOperations<String, String, Tconfig> hashOperations= redisTemplate.opsForHash();
        Tconfig tconfig=hashOperations.get(CONFIG_KEY_NAME,name);

        if(null!=tconfig)
        {
            return tconfig.getValue();
        }
        return null;
    }

    @Override
    public List<Tconfig> selectConfigList() {
        HashOperations<String, String, Tconfig> hashOperations= redisTemplate.opsForHash();
        List<Tconfig> tconfigList=hashOperations.values(CONFIG_KEY_NAME);
        return tconfigList;
    }
}
