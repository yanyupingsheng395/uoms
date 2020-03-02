package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.ActivityUserGroupMapper;
import com.linksteady.operate.domain.ActivityGroup;
import com.linksteady.operate.service.ActivityUserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2019-11-02
 */
@Service
public class ActivityUserGroupServiceImpl implements ActivityUserGroupService {

    @Autowired
    private ActivityUserGroupMapper activityUserGroupMapper;

    @Override
    public List<ActivityGroup> getUserGroupPage(String headId, String stage, int start, int end) {
        return activityUserGroupMapper.getUserGroupPage(headId, stage, start, end);
    }

    @Override
    public List<ActivityGroup> getUserGroupList(String headId, String stage, String type) {
        List<ActivityGroup> userGroupList = activityUserGroupMapper.getUserGroupList(headId, stage);
        List<ActivityGroup> result = userGroupList.stream().filter(x -> type.equals(x.getActivityType())).collect(Collectors.toList());
        return result;
    }

    @Override
    public int getCount(String headId, String stage) {
        return activityUserGroupMapper.getCount(headId, stage);
    }

    @Override
    public void updateGroupTemplate(String headId, String groupId, String code, String stage) {
        activityUserGroupMapper.updateGroupTemplate(headId, groupId, code, stage);
    }

    @Override
    public List<ActivityGroup> getActivityUserList(String headId, String stage) {
        List<ActivityGroup> userGroupList = activityUserGroupMapper.getUserGroupList(headId, stage);
        return userGroupList;
    }

    @Override
    public int validGroupTemplate(String headId, String stage) {
        return activityUserGroupMapper.validGroupTemplate(headId, stage);
    }

    @Override
    public int refrenceCount(String code) {
        return activityUserGroupMapper.refrenceCount(code);
    }

    @Override
    public void deleteData(String headId) {
        activityUserGroupMapper.deleteData(headId);
    }

    @Override
    public void setSmsCode(String groupId, String tmpCode, String headId, String type, String stage) {
        activityUserGroupMapper.setSmsCode(groupId, tmpCode, headId, type, stage);
    }

    @Override
    public boolean checkTmpIsUsed(String tmpCode) {
        return activityUserGroupMapper.checkTmpIsUsed(tmpCode) > 0;
    }
}
