package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivityGroup;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-11-02
 */
public interface ActivityUserGroupMapper {

    int getCount(Long headId, String stage);

    List<ActivityGroup> getUserGroupPage(Long headId, String stage, int start, int end);

    List<ActivityGroup> getUserGroupList(Long headId, String stage);

    void updateGroupTemplate(Long headId, String groupId, String code, String stage);

    void saveGroupData(List<ActivityGroup> dataList);

    int refrenceCount(String code);

    void deleteData(Long headId);

    void setSmsCode(String groupId, String tmpCode, Long headId, String type, String stage);

    int checkTmpIsUsed(String tmpCode);

    void validUserGroupNotify(String headId, String stage);

    void validUserGroupDuring(String headId, String stage);

    int validGroupTemplate(Long headId, String stage, String type);

    int validGroupTemplateWithGroup(Long headId, String stage, String type, List<String> groupIds);

    void removeSmsSelected(String headId, String stage, String smsCode, String groupId);
}
