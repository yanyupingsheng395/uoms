package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.PushLargeListMapper;
import com.linksteady.operate.domain.PushListLager;
import com.linksteady.operate.service.PushLargeListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-10-23
 */
@Service
public class PushLargeListServiceImpl implements PushLargeListService {

    @Autowired
    private PushLargeListMapper pushLargeListMapper;

    @Override
    public int getPushListCount(int currentHour) {
        return pushLargeListMapper.getPushListCount(currentHour);
    }

    @Override
    public List<PushListLager> getPushList(int currentHour, String sms, int start, int end) {
        return pushLargeListMapper.getPushList(currentHour, sms, start, end);
    }

    @Override
    public List<String> getSmsContent(int currentHour) {
        return pushLargeListMapper.getSmsContent(currentHour);
    }

    @Override
    public void updatePushState(List<String> smsContent, Long maxPushId,int currentHour) {
        pushLargeListMapper.updatePushState(smsContent, maxPushId,currentHour);
    }

    @Override
    public int getPushListCountBySms(int currentHour, String sms) {
        return pushLargeListMapper.getPushListCountBySms(currentHour, sms);
    }

    @Override
    public Long getMaxPushId(int currentHour) {
        return pushLargeListMapper.getMaxPushId(currentHour);
    }
}
