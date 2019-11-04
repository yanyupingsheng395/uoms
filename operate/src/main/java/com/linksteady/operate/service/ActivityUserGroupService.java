package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityGroup;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-11-02
 */
public interface ActivityUserGroupService {

    List<ActivityGroup> getUserGroupPage(String headId, String stage, int start, int end);

    List<ActivityGroup> getUserGroupList(String headId, String stage);

    int getCount(String headId, String stage);
}
