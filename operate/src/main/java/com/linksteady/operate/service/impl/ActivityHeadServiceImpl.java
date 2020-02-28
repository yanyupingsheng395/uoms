package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Lists;
import com.linksteady.common.domain.User;
import com.linksteady.operate.dao.ActivityCovMapper;
import com.linksteady.operate.dao.ActivityHeadMapper;
import com.linksteady.operate.dao.ActivityTemplateMapper;
import com.linksteady.operate.dao.ActivityUserGroupMapper;
import com.linksteady.operate.domain.ActivityCovInfo;
import com.linksteady.operate.domain.ActivityGroup;
import com.linksteady.operate.domain.ActivityGroup;
import com.linksteady.operate.domain.ActivityHead;
import com.linksteady.operate.domain.ActivityTemplate;
import com.linksteady.operate.domain.enums.ActivityStageEnum;
import com.linksteady.operate.domain.enums.ActivityStatusEnum;
import com.linksteady.operate.domain.enums.ActivityStageEnum;
import com.linksteady.operate.domain.enums.ActivityStageEnum;
import com.linksteady.operate.service.ActivityHeadService;
import com.linksteady.operate.service.ActivityUserGroupService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    ActivityPlanServiceImpl activityPlanService;

    @Autowired
    private ActivityUserGroupService activityUserGroupService;

    @Autowired
    private ActivitySummaryService activitySummaryService;

    @Autowired
    private ActivityUserGroupMapper activityUserGroupMapper;

    @Autowired
    private ActivityCovMapper activityCovMapper;

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
        final String HAS_PREHEAT = "1";
        final String NO_PREHEAT = "0";
        String hasPreheat = activityHead.getHasPreheat();
        activityHead.setFormalStatus("edit");
        Long headId = activityHead.getHeadId();
        activityHead.setFormalStatus(ActivityStatusEnum.EDIT.getStatusCode());
        if (HAS_PREHEAT.equalsIgnoreCase(activityHead.getHasPreheat())) {
            activityHead.setPreheatStatus(ActivityStatusEnum.EDIT.getStatusCode());
        }
        if (NO_PREHEAT.equalsIgnoreCase(hasPreheat)) {
            activityHead.setPreheatStartDt(null);
            activityHead.setPreheatEndDt(null);
            activityHead.setPreheatNotifyDt(null);
        }
        activityHead.setInsertDt(new Date());
        activityHead.setInsertBy(((User)SecurityUtils.getSubject().getPrincipal()).getUsername());
        activityHeadMapper.saveActivityHead(activityHead);
//        //更新当前活动是大型活动还是小型活动的标记
//        activityHeadMapper.updateActivityFlag(activityHead.getHeadId().toString());
//        // 保存群组的初始化信息
//        activityUserGroupService.saveGroupData(activityHead.getHeadId().toString(), activityHead.getHasPreheat());
//        //写入计划信息
        activityPlanService.savePlanList(activityHead.getHeadId().toString(), activityHead.getHasPreheat());
        if(HAS_PREHEAT.equals(hasPreheat)) {
            // 保存群组信息
            saveGroupInfo(activityHead.getHeadId().toString(), ActivityStageEnum.preheat.getStageCode());
            saveGroupInfo(activityHead.getHeadId().toString(), ActivityStageEnum.formal.getStageCode());

            // 保存转化率信息
            saveCovInfo(activityHead.getHeadId().toString(), ActivityStageEnum.preheat.getStageCode());
            saveCovInfo(activityHead.getHeadId().toString(), ActivityStageEnum.formal.getStageCode());
        }else {
            // 保存群组信息
            saveGroupInfo(activityHead.getHeadId().toString(), ActivityStageEnum.formal.getStageCode());

            // 保存转化率信息
            saveCovInfo(activityHead.getHeadId().toString(), ActivityStageEnum.formal.getStageCode());
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
        updateStatus(headId, stage, "todo");
    }

    @Override
    public Map<String, String> getDataChangedStatus(String headId, String stage) {
        return activityHeadMapper.getDataChangedStatus(headId, stage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteData(String headId) {
        activityHeadMapper.deleteActivity(headId);
    }

    @Override
    public int getDeleteCount(String headId) {
        return activityHeadMapper.getDeleteCount(headId);
    }

    @Override
    public String getStatus(String headId, String stage) {
        String sql = "";
        if (ActivityStageEnum.preheat.getStageCode().equalsIgnoreCase(stage)) {
            sql = "select preheat_status from UO_OP_ACTIVITY_HEADER where head_id = '" + headId + "'";
        }
        if (ActivityStageEnum.formal.getStageCode().equalsIgnoreCase(stage)) {
            sql = "select formal_status from UO_OP_ACTIVITY_HEADER where head_id = '" + headId + "'";
        }
        return activityHeadMapper.getStatus(sql);
    }

    @Override
    public void updateStatus(String headId, String stage, String status) {
        if (ActivityStageEnum.preheat.getStageCode().equalsIgnoreCase(stage)) {
            activityHeadMapper.updatePreheatStatusHead(headId,status);
        }else if(ActivityStageEnum.formal.getStageCode().equalsIgnoreCase(stage)) {
            activityHeadMapper.updateFormalStatusHead(headId,status);
        }
    }

    @Override
    public void saveSmsTemplate(ActivityTemplate activityTemplate) {
        activityTemplate.setInsertBy((((User)SecurityUtils.getSubject().getPrincipal()).getUsername()));
        activityTemplate.setInsertDt(new Date());
        if("0".equals(activityTemplate.getIsProdUrl()) && "0".equals(activityTemplate.getIsProdName()) && "0".equals(activityTemplate.getIsPrice())) {
            activityTemplate.setIsPersonal("0");
        }else {
            activityTemplate.setIsPersonal("1");
        }
        activityHeadMapper.saveSmsTemplate(activityTemplate);
    }

    @Override
    public List<ActivityTemplate> getSmsTemplateList(ActivityTemplate activityTemplate) {
        List<ActivityTemplate> templateTableData = activityHeadMapper.getTemplateTableData(activityTemplate);
        templateTableData.stream().map(x->{
            String relation = x.getRelation();
            if(StringUtils.isNotEmpty(relation)) {
                relation = relation.replace("GROWTH", "成长").replace("LATENT", "潜在");
            }
            String scene = x.getScene();
            if(StringUtils.isNotEmpty(scene)) {
                scene = scene.replace("DURING", "活动期间").replace("NOTIFY", "活动通知");
            }
            x.setRelation(relation);
            x.setScene(scene);
            return x;
        }).collect(Collectors.toList());
        return templateTableData;
    }

    /**
     * 保存cov_info表
     * 如果已有记录直接update，否则insert
     */
    private void saveCovInfo(String headId, String stage) {
        String covId = activityCovMapper.getCovId(headId, stage);
        if(StringUtils.isEmpty(covId)) {
            covId = activityCovMapper.getCovList("Y").get(0).getCovListId();
        }
        if(ActivityStageEnum.preheat.getStageCode().equals(stage)) {
            activityCovMapper.updateFormalCovInfo(headId, covId);
        }
        if(ActivityStageEnum.formal.getStageCode().equals(stage)) {
            activityCovMapper.updateFormalCovInfo(headId, covId);
        }
    }

    /**
     * 保存群组信息
     * @param headId
     */
    private void saveGroupInfo(String headId, String activityStage) {
        //类型 NOTIFY 通知 DURING 期间
        List<ActivityGroup> activityGroups = Lists.newArrayList();
        activityGroups.add(new ActivityGroup(
                1L, Long.parseLong(headId), "用户成长旅程的商品参与本次活动", activityStage, "NOTIFY", ((User)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date()
        ));
        activityGroups.add(new ActivityGroup(
                2L, Long.parseLong(headId), "用户成长旅程的商品没有参与本次活动", activityStage, "NOTIFY", ((User)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date()
        ));
        activityGroups.add(new ActivityGroup(
                3L, Long.parseLong(headId), "用户成长旅程的商品没有参与本次活动，但有可能成为活动商品潜在用户", activityStage, "NOTIFY", ((User)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date()
        ));

        activityGroups.add(new ActivityGroup(
                1L, Long.parseLong(headId), "用户成长旅程的商品参与本次活动", activityStage, "DURING", ((User)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date()
        ));
        activityGroups.add(new ActivityGroup(
                2L, Long.parseLong(headId), "用户成长旅程的商品没有参与本次活动", activityStage, "DURING", ((User)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date()
        ));

        activityUserGroupMapper.saveGroupData(activityGroups);
    }
}