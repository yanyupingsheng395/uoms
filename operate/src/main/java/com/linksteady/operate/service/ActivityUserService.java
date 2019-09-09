package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityUser;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-09-07
 */
public interface ActivityUserService {

    List<ActivityUser> getActivityUserListPage(int start, int end, String startDate, String endDate);

    int getCount(String startDate, String endDate);
}
