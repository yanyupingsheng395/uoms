package com.linksteady.operate.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.operate.domain.TgtMonitor;

import java.util.List;

public interface TgtMonitorMapper extends MyMapper<TgtMonitor> {
    List<TgtMonitor> getDataByTgtId(String id, String type);
}