package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.DailyGroupMapper;
import com.linksteady.operate.domain.DailyGroup;
import com.linksteady.operate.service.DailyGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-07-31
 */
@Service
public class DailyGroupServiceImpl implements DailyGroupService {

    @Autowired
    private DailyGroupMapper dailyGroupMapper;

    @Override
    public List<DailyGroup> getDataList(String headId) {
        return dailyGroupMapper.getDataList(headId);
    }
}
