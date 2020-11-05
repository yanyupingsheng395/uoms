package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityGroup;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-11-02
 */
public interface QywxActivityUserGroupService {

    List<ActivityGroup> getUserGroupList(Long headId, String type);

    int getCount(Long headId);

    void updateGroupTemplate(Long headId, Long groupId, Long code);

    int validGroupTemplateWithGroup(Long headId, String type);

    void validUserGroup(String headId);
}
