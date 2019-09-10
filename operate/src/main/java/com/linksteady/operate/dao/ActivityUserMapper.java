package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivityUser;

import java.util.LinkedList;
import java.util.List;

/**
 * @author hxcao
 * @date 2019-09-07
 */
public interface ActivityUserMapper {

    List<ActivityUser> getActivityUserListPage(int start, int end, String startDate, String endDate, Long dayPeriod);

    int getCount(String startDate, String endDate, Long dayPeriod);

    LinkedList<ActivityUser> getActivityUser(String startDate, String endDate, Long dayPeriod);

    List<ActivityUser> getActivityUserByDateAndProductId(String productId, String startDate, String endDate);

    Long getCoverUserCnt(String startDate, String endDate, Long dayPeriod);
}
