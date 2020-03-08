package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityGroup;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-11-02
 */
public interface ActivityUserGroupService {

    List<ActivityGroup> getUserGroupPage(Long headId, String stage, int start, int end);

    List<ActivityGroup> getUserGroupList(Long headId, String stage, String type);

    int getCount(Long headId, String stage);

    void updateGroupTemplate(Long headId, String groupId, String code, String stage);

    List<ActivityGroup> getActivityUserList(Long headId, String stage);

    int validGroupTemplate(Long headId, String stage);

    int refrenceCount(String code);

    void deleteData(Long headId);

    void setSmsCode(String groupId, String tmpCode, Long headId, String type, String stage);

    boolean checkTmpIsUsed(String tmpCode);
}
