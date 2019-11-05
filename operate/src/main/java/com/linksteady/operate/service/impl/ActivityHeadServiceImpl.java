package com.linksteady.operate.service.impl;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.dao.ActivityHeadMapper;
import com.linksteady.operate.dao.ActivityUserMapper;
import com.linksteady.operate.domain.ActivityHead;
import com.linksteady.operate.domain.ActivityPlan;
import com.linksteady.operate.domain.ActivityTemplate;
import com.linksteady.operate.domain.ActivityUser;
import com.linksteady.operate.service.ActivityHeadService;
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

    @Autowired
    private ActivityHeadMapper activityHeadMapper;

    @Override
    public List<ActivityHead> getDataListOfPage(int start, int end, String name) {
        return activityHeadMapper.getDataListOfPage(start, end, name);
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

    @Override
    public void submitActivity(String headId, String stage) {
        StringBuffer sb = new StringBuffer();
        sb.append("update UO_OP_ACTIVITY_HEADER set ");
        if (stage.equalsIgnoreCase("preheat")) {
            sb.append("PREHEAT_STATUS = 'todo'");
        }
        if (stage.equalsIgnoreCase("formal")) {
            sb.append("FORMAL_STATUS = 'todo'");
        }
        sb.append(" where head_id = '" + headId + "'");
        activityHeadMapper.submitActivity(sb.toString());
    }
}