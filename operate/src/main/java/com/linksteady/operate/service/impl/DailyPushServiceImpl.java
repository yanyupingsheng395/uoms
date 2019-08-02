package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.DailyPushMapper;
import com.linksteady.operate.domain.DailyPush;
import com.linksteady.operate.service.DailyPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-08-02
 */
@Service
public class DailyPushServiceImpl implements DailyPushService {

    @Autowired
    private DailyPushMapper dailyPushMapper;

    @Override
    public List<DailyPush> getPageList(int start, int end, String headId) {
        return dailyPushMapper.getPushList(start, end, headId);
    }

    @Override
    public int getDataTotalCount(String headId) {
        return dailyPushMapper.getDataTotalCount(headId);
    }
}
