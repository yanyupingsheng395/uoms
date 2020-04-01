package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.linksteady.common.domain.User;
import com.linksteady.operate.dao.ActivityCovMapper;
import com.linksteady.operate.dao.ActivityHeadMapper;
import com.linksteady.operate.dao.ActivityUserGroupMapper;
import com.linksteady.operate.domain.ActivityGroup;
import com.linksteady.operate.domain.ActivityHead;
import com.linksteady.operate.domain.enums.ActivityPlanTypeEnum;
import com.linksteady.operate.domain.enums.ActivityStageEnum;
import com.linksteady.operate.domain.enums.ActivityStatusEnum;
import com.linksteady.operate.service.ActivityHeadService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-08-13
 */
@Service
public class ActivityHeadServiceImpl implements ActivityHeadService {

    @Autowired
    private ActivityHeadMapper activityHeadMapper;

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

        activityHead.setFormalStatus(ActivityStatusEnum.EDIT.getStatusCode());
        activityHead.setFormalNotifyStatus(ActivityStatusEnum.EDIT.getStatusCode());
        if (HAS_PREHEAT.equalsIgnoreCase(activityHead.getHasPreheat())) {
            activityHead.setPreheatStatus(ActivityStatusEnum.EDIT.getStatusCode());
            activityHead.setPreheatNotifyStatus(ActivityStatusEnum.EDIT.getStatusCode());
        }
        if (NO_PREHEAT.equalsIgnoreCase(hasPreheat)) {
            activityHead.setPreheatStartDt(null);
            activityHead.setPreheatEndDt(null);
            activityHead.setPreheatNotifyDt(null);
            activityHead.setPreheatNotifyStatus(null);
        }
        activityHead.setInsertDt(new Date());
        activityHead.setInsertBy(((User)SecurityUtils.getSubject().getPrincipal()).getUsername());
        activityHeadMapper.saveActivityHead(activityHead);
        if(HAS_PREHEAT.equals(hasPreheat)) {
            // 保存群组信息
            saveGroupInfo(activityHead.getHeadId(), ActivityStageEnum.preheat.getStageCode());
            saveGroupInfo(activityHead.getHeadId(), ActivityStageEnum.formal.getStageCode());

            // 保存转化率信息
            saveCovInfo(activityHead.getHeadId(), ActivityStageEnum.preheat.getStageCode());
            saveCovInfo(activityHead.getHeadId(), ActivityStageEnum.formal.getStageCode());
        }else {
            // 保存群组信息
            saveGroupInfo(activityHead.getHeadId(), ActivityStageEnum.formal.getStageCode());

            // 保存转化率信息
            saveCovInfo(activityHead.getHeadId(), ActivityStageEnum.formal.getStageCode());
        }
        return activityHead.getHeadId().intValue();
    }

    @Override
    public ActivityHead findById(Long headId) {
        return activityHeadMapper.findById(headId);
    }

    @Override
    public String getActivityName(String headId) {
        return activityHeadMapper.getActivityName(headId);
    }

    @Override
    public int getActivityStatus(String id) {
        return activityHeadMapper.getActivityStatus(id);
    }


    @Override
    public Map<String, String> getDataChangedStatus(Long headId, String stage) {
        return activityHeadMapper.getDataChangedStatus(headId, stage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteData(Long headId) {
        activityHeadMapper.deleteActivity(headId);
    }

    @Override
    public int getDeleteCount(Long headId) {
        return activityHeadMapper.getDeleteCount(headId);
    }

    /**
     *
     * @param headId
     * @param stage  活动阶段
     * @param status
     * @param type   计划类型 通知 or 正式
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long headId, String stage, String status, String type) {
        if (ActivityStageEnum.preheat.getStageCode().equalsIgnoreCase(stage)) {
            if(ActivityPlanTypeEnum.Notify.getPlanTypeCode().equalsIgnoreCase(type)) {
                activityHeadMapper.updatePreheatStatusHead(headId,status,ActivityPlanTypeEnum.Notify.getPlanTypeCode());
            } else if(ActivityPlanTypeEnum.During.getPlanTypeCode().equalsIgnoreCase(type)) {
                activityHeadMapper.updatePreheatStatusHead(headId,status,ActivityPlanTypeEnum.During.getPlanTypeCode());
            }
        }else if(ActivityStageEnum.formal.getStageCode().equalsIgnoreCase(stage)) {
            if(ActivityPlanTypeEnum.Notify.getPlanTypeCode().equalsIgnoreCase(type)) {
                activityHeadMapper.updateFormalStatusHead(headId,status,ActivityPlanTypeEnum.Notify.getPlanTypeCode());
            } else if(ActivityPlanTypeEnum.During.getPlanTypeCode().equalsIgnoreCase(type)) {
                activityHeadMapper.updateFormalStatusHead(headId,status,ActivityPlanTypeEnum.During.getPlanTypeCode());
            }
        }
    }

    /**
     * 保存cov_info表
     * 如果已有记录直接update，否则insert
     */
    private void saveCovInfo(long headId, String stage) {
        String covId = activityCovMapper.getCovId(headId, stage);
        if(StringUtils.isEmpty(covId)) {
            covId = activityCovMapper.getCovList("Y").get(0).getCovListId();
        }
        if(ActivityStageEnum.preheat.getStageCode().equals(stage)) {
            activityCovMapper.updatePreheatCovInfo(headId, covId);
        }
        if(ActivityStageEnum.formal.getStageCode().equals(stage)) {
            activityCovMapper.updateFormalCovInfo(headId, covId);
        }
    }

    /**
     * 保存群组信息
     * @param headId
     */
    private void saveGroupInfo(long headId, String activityStage) {
        //类型 NOTIFY 通知 DURING 期间
        List<ActivityGroup> activityGroups = Lists.newArrayList();
        activityGroups.add(new ActivityGroup(
                1L,headId, "活动价", activityStage, "NOTIFY", ((User)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "Y"
        ));
        activityGroups.add(new ActivityGroup(
                2L, headId, "满件打折", activityStage, "NOTIFY", ((User)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "Y"
        ));
        activityGroups.add(new ActivityGroup(
                3L, headId, "满元减钱", activityStage, "NOTIFY", ((User)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "Y"
        ));
        activityGroups.add(new ActivityGroup(
                4L, headId, "特价", activityStage, "NOTIFY", ((User)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "Y"
        ));
        activityGroups.add(new ActivityGroup(
                5L, headId, "——", activityStage, "NOTIFY", ((User)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "N"
        ));

        activityGroups.add(new ActivityGroup(
                6L,headId, "推荐成长商品", activityStage, "DURING", ((User)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "Y"
        ));
        activityGroups.add(new ActivityGroup(
                7L, headId, "推荐潜在商品", activityStage, "DURING", ((User)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "Y"
        ));
        activityGroups.add(new ActivityGroup(
                8L, headId, "推荐成长商品", activityStage, "DURING", ((User)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "N"
        ));
        activityUserGroupMapper.saveGroupData(activityGroups);
    }

}