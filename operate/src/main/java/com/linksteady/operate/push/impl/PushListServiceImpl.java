package com.linksteady.operate.push.impl;

import com.linksteady.operate.dao.PushListMapper;
import com.linksteady.operate.domain.PushListInfo;
import com.linksteady.operate.push.PushListService;
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
    public int getPendingPushMaxId(int currHour) {
        return pushListMapper.getPendingPushMaxId(currHour);
    }

    @Override
    public int getPendingPushCount(int maxPushId,int currHour) {
        return pushListMapper.getPendingPushCount(maxPushId,currHour);
    }

    @Override
    public List<PushListInfo> getPendingPushList(int maxPushId, int start, int end,int currHour) {
        return pushListMapper.getPendingPushList(maxPushId,start,end,currHour);
    }

    /**
     * 更新当前批次的IS_PUSH字段
     */
    @Override
    public void updateIsPush(int maxPushId,int currHour) {
        pushListMapper.updateIsPush(maxPushId,currHour);
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
