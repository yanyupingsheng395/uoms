package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.*;
import com.linksteady.operate.domain.ActivityGroup;
import com.linksteady.operate.domain.ActivityHead;
import com.linksteady.operate.service.ActivityUserGroupService;
import com.linksteady.operate.service.QywxActivityUserGroupService;
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
public class QywxActivityUserGroupServiceImpl implements QywxActivityUserGroupService {

    @Autowired
    private QywxActivityUserGroupMapper activityUserGroupMapper;

    @Autowired
    private QywxActivityTemplateMapper activityTemplateMapper;

    @Override
    public List<ActivityGroup> getUserGroupList(Long headId, String type) {
        //对活动当前stage的组上配置的文案情况进行一次校验
        activityTemplateMapper.validUserGroup(headId, type);
        List<ActivityGroup> userGroupList = activityUserGroupMapper.getUserGroupList(headId);
        //仅返回当前活动类型的组列表
        List<ActivityGroup> result = userGroupList.stream().filter(x -> type.equals(x.getActivityType())).collect(Collectors.toList());
        return result;
    }

    @Override
    public int getCount(Long headId) {
        return activityUserGroupMapper.getCount(headId);
    }

    @Override
    public void updateGroupTemplate(Long headId, Long groupId, Long code) {
        activityUserGroupMapper.updateGroupTemplate(headId, groupId, code);
    }

    /**
     * 验证当前活动（某个阶段，类型） 活动商品对应的活动类型是否都配置了文案 返回未设置文件的组数
     * @param headId
     * @param type
     * @return
     */
    @Override
    public int validGroupTemplateWithGroup(Long headId, String type) {
        return activityUserGroupMapper.validGroupTemplateWithGroup(headId, type);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void validUserGroup(String headId) {
//        ActivityHead activityHead = activityHeadMapper.findById(Long.valueOf(headId));
//        if(stage.equalsIgnoreCase("preheat")) {
//            String preheatNotifyStatus = activityHead.getPreheatNotifyStatus();
//            if(preheatNotifyStatus.equalsIgnoreCase("edit") || preheatNotifyStatus.equalsIgnoreCase("todo")) {
//                activityTemplateMapper.validUserGroup(Long.valueOf(headId), stage, "NOTIFY");
//            }
//            String preheatDuringStatus = activityHead.getPreheatStatus();
//            if(preheatDuringStatus.equalsIgnoreCase("edit") || preheatDuringStatus.equalsIgnoreCase("todo")) {
//                activityTemplateMapper.validUserGroup(Long.valueOf(headId), stage, "DURING");
//            }
//        }
//
//        if(stage.equalsIgnoreCase("formal")) {
//            String formalNotifyStatus = activityHead.getFormalNotifyStatus();
//            if(formalNotifyStatus.equalsIgnoreCase("edit") || formalNotifyStatus.equalsIgnoreCase("todo")) {
//                activityTemplateMapper.validUserGroup(Long.valueOf(headId), stage, "NOTIFY");
//            }
//            String formalStatus = activityHead.getFormalStatus();
//            if(formalStatus.equalsIgnoreCase("edit") || formalStatus.equalsIgnoreCase("todo")) {
//                activityTemplateMapper.validUserGroup(Long.valueOf(headId), stage, "DURING");
//            }
//        }
    }
}
