package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityGroup;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-11-02
 */
public interface ActivityUserGroupService {

    List<ActivityGroup> getUserGroupPage(Long headId, String stage, int limit, int offset);

    List<ActivityGroup> getUserGroupList(Long headId, String stage, String type);

    int getCount(Long headId, String stage);

    void updateGroupTemplate(Long headId, String groupId, String code, String stage);

    int refrenceCount(String code);

    void deleteData(Long headId);

    int validGroupTemplateWithGroup(Long headId, String stage, String type);


}
