package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.linksteady.operate.service.DiagConditionService;
import org.crazycake.shiro.RedisSerializer;
import org.crazycake.shiro.RedisSessionDAO;
import org.crazycake.shiro.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiagConditionServiceImpl implements DiagConditionService {

    @Autowired
    private RedisSessionDAO redisSessionDAO;

    private static final String PREFIX = "node_";

    @Override
    public void redisCreate(String data, String diagId, String nodeId) throws Exception{
        JSONArray jsonArray = JSON.parseArray(data);
        RedisSerializer serializer = new StringSerializer();
        String key = PREFIX + diagId + "_" + nodeId;
        redisSessionDAO.getRedisManager().set(serializer.serialize(key), serializer.serialize(jsonArray), 1800);
    }

    @Override
    public String redisRead(String diagId, String nodeId) throws Exception{
        RedisSerializer serializer = new StringSerializer();
        byte[] result = redisSessionDAO.getRedisManager().get(serializer.serialize(PREFIX + diagId + "_" + nodeId));
        return ((StringSerializer) serializer).deserialize(result);
    }
}
