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
    public int getPushLargeListCount(int currentHour) {
        return pushLargeListMapper.getPushLargeListCount(currentHour);
    }

    @Override
    public List<PushListLager> getPushLargeList(int currentHour, String sms, int start, int end) {
        return pushLargeListMapper.getPushLargeList(currentHour, sms, start, end);
    }

    @Override
    public List<String> getSmsContentList(int currentHour) {
        return pushLargeListMapper.getSmsContentList(currentHour);
    }

    @Override
    public int getPushListCountBySms(int currentHour, String sms) {
        return pushLargeListMapper.getPushListCountBySms(currentHour, sms);
    }

}
