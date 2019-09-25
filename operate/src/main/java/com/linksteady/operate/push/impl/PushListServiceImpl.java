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
    public int getPendingPushMaxId() {
        return pushListMapper.getPendingPushMaxId();
    }

    @Override
    public int getPendingPushCount(int maxPushId) {
        return pushListMapper.getPendingPushCount(maxPushId);
    }

    @Override
    public List<PushListInfo> getPendingPushList(int maxPushId, int start, int end) {
        return pushListMapper.getPendingPushList(maxPushId,start,end);
    }

    /**
     * 更新当前批次的IS_PUSH字段
     */
    @Override
    public void updateIsPush(int maxPushId) {
        pushListMapper.updateIsPush(maxPushId);
    }


}
