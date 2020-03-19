package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.linksteady.common.domain.User;
import com.linksteady.operate.dao.ActivityCovMapper;
import com.linksteady.operate.dao.ActivityHeadMapper;
import com.linksteady.operate.dao.ActivityUserGroupMapper;
import com.linksteady.operate.domain.ActivityGroup;
import com.linksteady.operate.domain.ActivityHead;
import com.linksteady.operate.domain.ActivityTemplate;
import com.linksteady.operate.domain.enums.ActivityStageEnum;
import com.linksteady.operate.domain.enums.ActivityStatusEnum;
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

    @Autowired
    private ActivityHeadMapper activityHeadMapper;

    @Autowired
    ActivityPlanServiceImpl activityPlanService;

    @Autowired
    private ActivityUserGroupService activityUserGroupService;

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

    @Override
    public String getStatus(Long headId, String stage) {
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
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long headId, String stage, String status, String type) {
        String during = "DURING";
        String notify = "NOTIFY";
        if (ActivityStageEnum.preheat.getStageCode().equalsIgnoreCase(stage)) {
            if(notify.equalsIgnoreCase(type)) {
                activityHeadMapper.updatePreheatNotifyStatusHead(headId,status);
            } else if(during.equalsIgnoreCase(type)) {
                activityHeadMapper.updatePreheatStatusHead(headId,status);
            }
        }else if(ActivityStageEnum.formal.getStageCode().equalsIgnoreCase(stage)) {
            if(notify.equalsIgnoreCase(type)) {
                activityHeadMapper.updateFormalNotifyStatusHead(headId,status);
            } else if(during.equalsIgnoreCase(type)) {
                activityHeadMapper.updateFormalStatusHead(headId,status);
            }
        }
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
    private void saveGroupInfo(String headId, String activityStage) {
        //类型 NOTIFY 通知 DURING 期间
        List<ActivityGroup> activityGroups = Lists.newArrayList();
        activityGroups.add(new ActivityGroup(
                1L, Long.parseLong(headId), "活动价", activityStage, "NOTIFY", ((User)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "Y"
        ));
        activityGroups.add(new ActivityGroup(
                2L, Long.parseLong(headId), "满件打折", activityStage, "NOTIFY", ((User)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "Y"
        ));
        activityGroups.add(new ActivityGroup(
                3L, Long.parseLong(headId), "满元减钱", activityStage, "NOTIFY", ((User)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "Y"
        ));
        activityGroups.add(new ActivityGroup(
                4L, Long.parseLong(headId), "特价", activityStage, "NOTIFY", ((User)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "Y"
        ));
        activityGroups.add(new ActivityGroup(
                5L, Long.parseLong(headId), "——", activityStage, "NOTIFY", ((User)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "N"
        ));

        activityGroups.add(new ActivityGroup(
                6L, Long.parseLong(headId), "推荐成长商品", activityStage, "DURING", ((User)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "Y"
        ));
        activityGroups.add(new ActivityGroup(
                7L, Long.parseLong(headId), "推荐潜在商品", activityStage, "DURING", ((User)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "Y"
        ));
        activityGroups.add(new ActivityGroup(
                8L, Long.parseLong(headId), "推荐成长商品", activityStage, "DURING", ((User)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "N"
        ));
        activityUserGroupMapper.saveGroupData(activityGroups);
    }

    /**
     * 获取头表状态
     * @param headId
     * @param stage
     * @return
     */
    private String getHeadStatus(Long headId, String stage) {
        String status = "";
        if(ActivityStageEnum.preheat.getStageCode().equalsIgnoreCase(stage)) {
            status = getStatus(headId, stage);
        }
        if(ActivityStageEnum.formal.getStageCode().equalsIgnoreCase(stage)) {
            status = getStatus(headId, stage);
        }
        return status;
    }
}