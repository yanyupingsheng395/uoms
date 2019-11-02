package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.ActivityUserGroupMapper;
import com.linksteady.operate.dao.ActivityUserMapper;
import com.linksteady.operate.domain.ActivityGroup;
import com.linksteady.operate.service.ActivityUserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-11-02
 */
@Service
public class ActivityUserGroupServiceImpl implements ActivityUserGroupService {

    @Autowired
    private ActivityUserGroupMapper activityUserGroupMapper;

    @Override
    public List<ActivityGroup> getUserGroupPage(String headId, String stage, int start, int end) {
        return activityUserGroupMapper.getUserGroupPage(headId, stage, start, end);
    }

    @Override
    public int getCount(String headId, String stage) {
        return activityUserGroupMapper.getCount(headId, stage);
    }
}
