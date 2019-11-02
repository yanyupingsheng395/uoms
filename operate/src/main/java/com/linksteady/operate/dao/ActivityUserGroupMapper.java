package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivityGroup;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-11-02
 */
public interface ActivityUserGroupMapper {

    int getCount(String headId, String stage);

    List<ActivityGroup> getUserGroupPage(String headId, String stage, int start, int end);
}
