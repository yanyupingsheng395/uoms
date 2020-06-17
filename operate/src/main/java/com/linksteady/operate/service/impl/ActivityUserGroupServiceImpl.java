package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.ActivityTemplateMapper;
import com.linksteady.operate.dao.ActivityUserGroupMapper;
import com.linksteady.operate.domain.ActivityGroup;
import com.linksteady.operate.service.ActivityUserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private ActivityTemplateMapper activityTemplateMapper;

    @Override
    public List<ActivityGroup> getUserGroupPage(Long headId, String stage, int limit, int offset) {
        return activityUserGroupMapper.getUserGroupPage(headId, stage, limit,offset);
    }

    @Override
    public List<ActivityGroup> getUserGroupList(Long headId, String stage, String type) {
        //对活动当前stage的组上配置的文案情况进行一次校验
        activityTemplateMapper.validUserGroup(headId, stage);
        List<ActivityGroup> userGroupList = activityUserGroupMapper.getUserGroupList(headId, stage);
        //仅返回当前活动类型的组列表
        List<ActivityGroup> result = userGroupList.stream().filter(x -> type.equals(x.getActivityType())).collect(Collectors.toList());
        return result;
    }

    @Override
    public int getCount(Long headId, String stage) {
        return activityUserGroupMapper.getCount(headId, stage);
    }

    @Override
    public void updateGroupTemplate(Long headId, String groupId, String code, String stage) {
        activityUserGroupMapper.updateGroupTemplate(headId, groupId, code, stage);
    }

    @Override
    public int refrenceCount(String code) {
        return activityUserGroupMapper.refrenceCount(code);
    }

    @Override
    public void deleteData(Long headId) {
        activityUserGroupMapper.deleteData(headId);
    }


    /**
     * 验证当前活动（某个阶段，类型） 活动商品对应的活动类型是否都配置了文案 返回未设置文件的组数
     * @param headId
     * @param stage
     * @param type
     * @return
     */
    @Override
    public int validGroupTemplateWithGroup(Long headId, String stage, String type) {
        return activityUserGroupMapper.validGroupTemplateWithGroup(headId, stage, type);
    }

}
