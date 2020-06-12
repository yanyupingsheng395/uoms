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

    void setSmsCode(String groupId, String tmpCode, Long headId, String type, String stage);

    boolean checkTmpIsUsed(String tmpCode);

    void validUserGroup(String headId, String stage);

    int validGroupTemplate(Long headId, String stage, String type);

    int validGroupTemplateWithGroup(Long headId, String stage, String type, List<String> groupIds);

    void removeSmsSelected(String type, String headId, String stage, String smsCode, String groupId);
}
