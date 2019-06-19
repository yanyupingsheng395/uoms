package com.linksteady.operate.service;

public interface DiagConditionService {

    void redisCreate(String data, String diagId, String nodeId) throws Exception;

    String redisRead(String diagId, String nodeId) throws Exception;
}
