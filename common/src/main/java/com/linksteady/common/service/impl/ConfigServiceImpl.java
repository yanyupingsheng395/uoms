package com.linksteady.common.service.impl;

import com.google.common.collect.Lists;
import com.linksteady.common.config.DictCacheManager;
import com.linksteady.common.dao.ConfigMapper;
import com.linksteady.common.domain.Dict;
import com.linksteady.common.domain.Tconfig;
import com.linksteady.common.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hxcao on 2019-04-29
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
        List<Tconfig> tconfigList=configMapper.selectConfigList();

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
    public Map<String,String> selectDictByTypeCode(String typeCode) {
        DictCacheManager dictCacheManager=DictCacheManager.getInstance();
        Map<String,String> map=dictCacheManager.getdictMap(typeCode);

        if(null==map||map.keySet().size()==0)
        {
            //从数据库中取，并放入到缓存中
            List<Dict> dictList=configMapper.selectDictByTypeCode(typeCode);

            Map<String,String> tempMap=dictList.stream().collect(Collectors.toMap(Dict::getCode,Dict::getValue,(o1,o2)->o1, LinkedHashMap::new));
            dictCacheManager.setDictMap(typeCode,tempMap);

        }
        return dictCacheManager.getdictMap(typeCode);
    }

    @Override
    public void updateConfig(String name, String value) {
        int count=configMapper.updateConfig(name,value);

        if(count>0)
        {
            //根据name重新加载配置
            Tconfig tconfig=configMapper.getTconfigByName(name);
            HashOperations<String, String, Tconfig> hashOperations= redisTemplate.opsForHash();
            hashOperations.put(CONFIG_KEY_NAME,name,tconfig);
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
    public boolean configExists() {
        return redisTemplate.hasKey(CONFIG_KEY_NAME);
    }

    @Override
    public List<Tconfig> selectConfigListFromRedis() {
        HashOperations<String, String, Tconfig> hashOperations= redisTemplate.opsForHash();
        List<Tconfig> tconfigList=hashOperations.values(CONFIG_KEY_NAME);
        return tconfigList;
    }
}
