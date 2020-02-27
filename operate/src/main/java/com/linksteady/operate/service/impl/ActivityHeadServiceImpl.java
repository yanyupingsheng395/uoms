package com.linksteady.operate.service.impl;

import com.linksteady.common.domain.User;
import com.linksteady.operate.dao.ActivityHeadMapper;
import com.linksteady.operate.domain.ActivityHead;
import com.linksteady.operate.domain.ActivityTemplate;
import com.linksteady.operate.domain.enums.ActivityStageEnum;
import com.linksteady.operate.domain.enums.ActivityStatusEnum;
import com.linksteady.operate.service.ActivityHeadService;
import com.linksteady.operate.service.ActivityUserGroupService;
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
    ActivityPlanServiceImpl activityPlanService;

    @Autowired
    private ActivityUserGroupService activityUserGroupService;

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
        activityHead.setFormalStatus(ActivityStatusEnum.EDIT.getStatusCode());
        if ("1".equalsIgnoreCase(activityHead.getHasPreheat())) {
            activityHead.setPreheatStatus(ActivityStatusEnum.EDIT.getStatusCode());
        }
        if ("0".equalsIgnoreCase(activityHead.getHasPreheat())) {
            activityHead.setPreheatStartDt(null);
            activityHead.setPreheatEndDt(null);
            activityHead.setPreheatNotifyDt(null);
        }
        activityHead.setInsertDt(new Date());
        activityHead.setInsertBy(((User)SecurityUtils.getSubject().getPrincipal()).getUsername());
        activityHeadMapper.saveActivityHead(activityHead);
//        // 保存群组的初始化信息
//        activityUserGroupService.saveGroupData(activityHead.getHeadId().toString(), activityHead.getHasPreheat());
         //写入计划信息
         activityPlanService.savePlanList(activityHead.getHeadId().toString(), activityHead.getHasPreheat());
        if (headId == null) {
            activityHeadMapper.saveActivityHead(activityHead);

            // 保存群组的初始化信息
            activityUserGroupService.saveGroupData(activityHead.getHeadId().toString(), activityHead.getHasPreheat());

            //写入计划信息
            activityPlanService.savePlanList(activityHead.getHeadId().toString(), activityHead.getHasPreheat());
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
}