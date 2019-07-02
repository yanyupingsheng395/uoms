package com.linksteady.operate.service;

public interface DiagDetailService {

    void save(String data);

    void updateAlarmFlag(String diagId, String nodeId, String flag);
}
