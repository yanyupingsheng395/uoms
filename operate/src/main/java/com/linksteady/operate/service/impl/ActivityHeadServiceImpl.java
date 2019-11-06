package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.linksteady.operate.dao.ActivityHeadMapper;
import com.linksteady.operate.dao.ActivityUserGroupMapper;
import com.linksteady.operate.domain.ActivityGroup;
import com.linksteady.operate.domain.ActivityHead;
import com.linksteady.operate.domain.ActivityPlan;
import com.linksteady.operate.domain.ActivityTemplate;
import com.linksteady.operate.service.ActivityHeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * @author hxcao
 * @date 2019-08-13
 */
@Service
public class ActivityHeadServiceImpl implements ActivityHeadService {

    // 活动阶段：预热
    private final String STAGE_PREHEAT = "preheat";
    // 活动阶段：正式
    private final String STAGE_FORMAL = "formal";

    @Autowired
    private ActivityHeadMapper activityHeadMapper;

    @Autowired
    private ActivityUserGroupMapper activityUserGroupMapper;

    @Override
    public List<ActivityHead> getDataListOfPage(int start, int end, String name, String date, String status) {
        return activityHeadMapper.getDataListOfPage(start, end, name, date, status);
    }

    @Override
    public int getDataCount(String name) {
        return activityHeadMapper.getDataCount(name);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveActivityHead(ActivityHead activityHead) {
        Long headId = activityHead.getHeadId();
        activityHead.setFormalStatus("edit");
        if ("1".equalsIgnoreCase(activityHead.getHasPreheat())) {
            activityHead.setPreheatStatus("edit");
        }
        if (headId == null) {
            activityHeadMapper.saveActivityHead(activityHead);
            // 保存群组的初始化信息
            saveGroupData(activityHead.getHeadId().toString(), activityHead.getHasPreheat());
        } else {
            activityHeadMapper.updateActiveHead(activityHead);
        }
        return activityHead.getHeadId().intValue();
    }

    @Override
    public ActivityHead findById(String headId) {
        return activityHeadMapper.findById(headId);
    }

    @Override
    public List<ActivityTemplate> getTemplateTableData() {
        return activityHeadMapper.getTemplateTableData();
    }

    @Override
    public List<ActivityPlan> getPlanList(String headId) {
        return activityHeadMapper.getPlanList(headId);
    }

    @Override
    public String getActivityName(String headId) {
        return activityHeadMapper.getActivityName(headId);
    }

    @Override
    public int getActivityStatus(String id) {
        return activityHeadMapper.getActivityStatus(id);
    }

    /**
     * @param headId
     * @param stage
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void submitActivity(String headId, String stage) {
        StringBuffer sb = new StringBuffer();
        sb.append("update UO_OP_ACTIVITY_HEADER set ");
        if (stage.equalsIgnoreCase(STAGE_PREHEAT)) {
            sb.append("PREHEAT_STATUS = 'todo'");
        }
        if (stage.equalsIgnoreCase(STAGE_FORMAL)) {
            sb.append("FORMAL_STATUS = 'todo'");
        }
        sb.append(" where head_id = '" + headId + "'");
        activityHeadMapper.submitActivity(sb.toString());
    }

    /**
     * 保存群组的初始化信息
     * @param headId
     * @param hasPreheat
     */
    @Transactional(rollbackFor = Exception.class)
    void saveGroupData(String headId, String hasPreheat) {
        List<ActivityGroup> dataList = Lists.newArrayList();
        ActivityGroup activityGroup = new ActivityGroup();
        activityGroup.setHeadId(Long.valueOf(headId));
        activityGroup.setGroupName("成长用户");
        activityGroup.setGroupId(1L);
        activityGroup.setInGrowthPath("在");
        activityGroup.setActiveLevel("活跃");
        activityGroup.setGroupUserCnt(0L);
        activityGroup.setActiveUserCnt(0L);
        activityGroup.setGrowthUserCnt(0L);
        dataList.add(activityGroup);

        activityGroup = new ActivityGroup();
        activityGroup.setHeadId(Long.valueOf(headId));
        activityGroup.setGroupName("成长用户");
        activityGroup.setGroupId(2L);
        activityGroup.setInGrowthPath("在");
        activityGroup.setActiveLevel("留存");
        activityGroup.setGroupUserCnt(0L);
        activityGroup.setActiveUserCnt(0L);
        activityGroup.setGrowthUserCnt(0L);
        dataList.add(activityGroup);

        activityGroup = new ActivityGroup();
        activityGroup.setHeadId(Long.valueOf(headId));
        activityGroup.setGroupName("成长用户");
        activityGroup.setGroupId(3L);
        activityGroup.setInGrowthPath("在");
        activityGroup.setActiveLevel("流失预警");
        activityGroup.setGroupUserCnt(0L);
        activityGroup.setActiveUserCnt(0L);
        activityGroup.setGrowthUserCnt(0L);
        dataList.add(activityGroup);

        activityGroup = new ActivityGroup();
        activityGroup.setHeadId(Long.valueOf(headId));
        activityGroup.setGroupName("成长用户");
        activityGroup.setGroupId(4L);
        activityGroup.setInGrowthPath("不在");
        activityGroup.setGroupUserCnt(0L);
        activityGroup.setGrowthUserCnt(0L);
        dataList.add(activityGroup);

        activityGroup = new ActivityGroup();
        activityGroup.setHeadId(Long.valueOf(headId));
        activityGroup.setGroupName("潜在用户");
        activityGroup.setGroupId(5L);
        activityGroup.setGroupUserCnt(0L);
        dataList.add(activityGroup);

        List<ActivityGroup> preheatList = Lists.newArrayList();
        List<ActivityGroup> formalList = Lists.newArrayList();
        dataList.stream().forEach(x->{
            try {
                preheatList.add((ActivityGroup) x.clone());
                formalList.add((ActivityGroup) x.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        });
        formalList.stream().forEach(x->{
            x.setActivityStage("formal");
        });
        if("1".equalsIgnoreCase(hasPreheat)) {
            preheatList.stream().forEach(x->{
                x.setActivityStage("preheat");
            });
            formalList.addAll(preheatList);
        }
        activityUserGroupMapper.saveGroupData(formalList);
    }
}