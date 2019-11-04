package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.PushListMapper;
import com.linksteady.operate.domain.PushListInfo;
import com.linksteady.operate.service.PushListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PushListServiceImpl implements PushListService {

    @Autowired
    private PushListMapper pushListMapper;


    @Override
    public int getPendingPushCount(int currHour) {
        return pushListMapper.getPendingPushCount(currHour);
    }

    @Override
    public List<PushListInfo> getPendingPushList(int limit,int currHour) {
        return pushListMapper.getPendingPushList(limit,currHour);
    }

    @Override
    public List<PushListInfo> getPushInfoListPage(int start, int end, String sourceCode, String pushStatus, String pushDateStr) {
        return pushListMapper.getPushInfoListPage(start, end, sourceCode, pushStatus, pushDateStr);
    }

    @Override
    public int getTotalCount(String sourceCode, String pushStatus, String pushDateStr) {
        return pushListMapper.getTotalCount(sourceCode, pushStatus, pushDateStr);
    }
}
