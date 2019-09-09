package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.ActivityUserMapper;
import com.linksteady.operate.domain.ActivityUser;
import com.linksteady.operate.service.ActivityUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-09-07
 */
@Service
public class ActivityUserServiceImpl implements ActivityUserService {

    @Autowired
    private ActivityUserMapper activityUserMapper;

    @Override
    public List<ActivityUser> getActivityUserListPage(int start, int end, String startDate, String endDate) {
        return activityUserMapper.getActivityUserListPage(start, end, startDate, endDate);
    }

    @Override
    public int getCount(String startDate, String endDate) {
        return activityUserMapper.getCount(startDate, endDate);
    }
}
