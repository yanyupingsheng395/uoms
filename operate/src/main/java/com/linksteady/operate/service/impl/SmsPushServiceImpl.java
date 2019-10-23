package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.SmsPushMapper;
import com.linksteady.operate.domain.PushListLager;
import com.linksteady.operate.service.SmsPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-10-23
 */
@Service
public class SmsPushServiceImpl implements SmsPushService {

    @Autowired
    private SmsPushMapper smsPushMapper;

    @Override
    public int getPushListCount(int currentHour) {
        return smsPushMapper.getPushListCount(currentHour);
    }

    @Override
    public List<PushListLager> getPushList(int currentHour, String sms, int start, int end) {
        return smsPushMapper.getPushList(currentHour, sms, start, end);
    }

    @Override
    public List<String> getSmsContent(int currentHour) {
        return smsPushMapper.getSmsContent(currentHour);
    }

    @Override
    public void updatePushState(List<String> smsContent, Long maxPushId) {
        smsPushMapper.updatePushState(smsContent, maxPushId);
    }

    @Override
    public int getPushListCountBySms(int currentHour, String sms) {
        return smsPushMapper.getPushListCountBySms(currentHour, sms);
    }

    @Override
    public Long getMaxPushId(int currentHour) {
        return smsPushMapper.getMaxPushId(currentHour);
    }
}
