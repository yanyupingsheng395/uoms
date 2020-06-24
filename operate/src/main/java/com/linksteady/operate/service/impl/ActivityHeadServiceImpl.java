package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.linksteady.common.bo.UserBo;
import com.linksteady.common.domain.User;
import com.linksteady.operate.dao.*;
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
    private ActivityProductMapper activityProductMapper;

    @Autowired
    private ActivityPlanMapper activityPlanMapper;

    @Autowired
    private ActivityDetailMapper activityDetailMapper;

    @Autowired
    private ActivityCovMapper activityCovMapper;

    @Override
    public List<ActivityHead> getDataListOfPage(int limit, int offset, String name, String date, String status) {
        return activityHeadMapper.getDataListOfPage(limit,offset, name, date, status);
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
        activityHead.setInsertBy(((UserBo)SecurityUtils.getSubject().getPrincipal()).getUsername());
        activityHeadMapper.saveActivityHead(activityHead);
        if(HAS_PREHEAT.equals(hasPreheat)) {
            saveGroupInfo(activityHead.getHeadId(), ActivityStageEnum.preheat.getStageCode());
            saveGroupInfo(activityHead.getHeadId(), ActivityStageEnum.formal.getStageCode());
        }else {
            saveGroupInfo(activityHead.getHeadId(), ActivityStageEnum.formal.getStageCode());
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
        activityProductMapper.deleteData(headId);
        activityPlanMapper.deletePlan(headId);
        activityUserGroupMapper.deleteData(headId);
        //删除活动明细数据
        activityDetailMapper.deleteData(headId);
        //删除转化率数据
        activityCovMapper.deleteData(headId);
    }

    /**
     * 判断当前活动是否可以删除
     * 可以删除的条件： 要么全部为待计划状态，要么全部为过期未执行状态
     * @param headId
     * @return
     */
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

    @Override
    public void expireActivityHead() {
        //失效预售通知
        activityHeadMapper.expirePreheatNotify();
        //失效预售正式
        activityHeadMapper.expirePreheatDuring();

        activityHeadMapper.expireFormalNotify();
        activityHeadMapper.expireFormalDuring();
    }
    /**
     * 保存群组信息
     * @param headId
     */
    private void saveGroupInfo(long headId, String activityStage) {
        //类型 NOTIFY 通知 DURING 期间
        List<ActivityGroup> activityGroups = Lists.newArrayList();
        activityGroups.add(new ActivityGroup(
                9L,headId, "满件打折", activityStage, "NOTIFY", ((UserBo)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "用户对该策略下的商品兴趣较高，且在本次活动中发生购买的概率较大"
        ));
        activityGroups.add(new ActivityGroup(
                10L, headId, "满元减钱", activityStage, "NOTIFY", ((UserBo)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "用户当前所处的价值、活跃度、年内购买次数维度，在历史活动中转化率较高,且在该商品上转化较高"
        ));
        activityGroups.add(new ActivityGroup(
                11L, headId, "特价秒杀", activityStage, "NOTIFY", ((UserBo)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "用户对该策略下的商品兴趣较高，且在本次活动中发生购买的概率较大"
        ));
        activityGroups.add(new ActivityGroup(
                12L, headId, "预售付尾立减", activityStage, "NOTIFY", ((UserBo)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "用户当前所处的价值、活跃度、年内购买次数维度，在历史活动中转化率较高,且在该商品上转化较高"
        ));
        activityGroups.add(new ActivityGroup(
                13L, headId, "无店铺活动", activityStage, "NOTIFY", ((UserBo)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "用户当前所处的价值、活跃度、年内购买次数维度，在历史活动中转化率较高,且在该商品上转化较高"
        ));

        activityGroups.add(new ActivityGroup(
                9L,headId, "满件打折", activityStage, "DURING", ((UserBo)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "用户对该策略下的商品兴趣较高，且在本次活动中发生购买的概率较大"
        ));
        activityGroups.add(new ActivityGroup(
                10L, headId, "满元减钱", activityStage, "DURING", ((UserBo)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "用户当前所处的价值、活跃度、年内购买次数维度，在历史活动中转化率较高,且在该商品上转化较高"
        ));
        activityGroups.add(new ActivityGroup(
                11L, headId, "特价秒杀", activityStage, "DURING", ((UserBo)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "用户对该策略下的商品兴趣较高，且在本次活动中发生购买的概率较大"
        ));
        activityGroups.add(new ActivityGroup(
                12L, headId, "预售付尾立减", activityStage, "DURING", ((UserBo)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "用户当前所处的价值、活跃度、年内购买次数维度，在历史活动中转化率较高,且在该商品上转化较高"
        ));
        activityGroups.add(new ActivityGroup(
                13L, headId, "无店铺活动", activityStage, "DURING", ((UserBo)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "用户当前所处的价值、活跃度、年内购买次数维度，在历史活动中转化率较高,且在该商品上转化较高"
        ));
        activityUserGroupMapper.saveGroupData(activityGroups);
    }
}