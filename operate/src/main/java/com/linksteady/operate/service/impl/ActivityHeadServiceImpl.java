package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.ActivityHeadMapper;
import com.linksteady.operate.domain.ActivityHead;
import com.linksteady.operate.domain.ActivityTemplate;
import com.linksteady.operate.service.ActivityHeadService;
import com.linksteady.operate.service.ActivitySummaryService;
import com.linksteady.operate.service.ActivityUserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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
        if ("0".equalsIgnoreCase(activityHead.getHasPreheat())) {
            activityHead.setPreheatStartDt(null);
            activityHead.setPreheatEndDt(null);
        }
        if (headId == null) {
            activityHeadMapper.saveActivityHead(activityHead);

            //更新当前活动是大型活动还是小型活动的标记
            activityHeadMapper.updateActivityFlag(activityHead.getHeadId().toString());

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
    public void updatePreheatHeadToDoing(String headId) {
        activityHeadMapper.updatePreheatHeadToDoing(headId);
    }

    @Override
    public void updateFormalHeadToDoing(String headId) {
        activityHeadMapper.updateFormalHeadToDoing(headId);
    }

    @Override
    public String getStatus(String headId, String stage) {
        String sql = "";
        if (STAGE_PREHEAT.equalsIgnoreCase(stage)) {
            sql = "select preheat_status from UO_OP_ACTIVITY_HEADER where head_id = '" + headId + "'";
        }
        if (STAGE_FORMAL.equalsIgnoreCase(stage)) {
            sql = "select formal_status from UO_OP_ACTIVITY_HEADER where head_id = '" + headId + "'";
        }
        return activityHeadMapper.getStatus(sql);
    }

    @Override
    public void updateStatus(String headId, String stage, String status) {
        StringBuffer sb = new StringBuffer();
        sb.append("update UO_OP_ACTIVITY_HEADER set ");
        if (stage.equalsIgnoreCase(STAGE_PREHEAT)) {
            sb.append("PREHEAT_STATUS = '"+status+"'");
        }
        if (stage.equalsIgnoreCase(STAGE_FORMAL)) {
            sb.append("FORMAL_STATUS = '"+status+"'");
        }
        sb.append(" where head_id = '" + headId + "'");
        activityHeadMapper.updateStatus(sb.toString());
    }
}