package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.ActivityHeadMapper;
import com.linksteady.operate.dao.ActivityUserMapper;
import com.linksteady.operate.domain.ActivityHead;
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
        return activityHeadMapper.getDataListOfPage(start, end ,name);
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
        if("1".equalsIgnoreCase(activityHead.getActivityStage())) {
            activityHead.setPreheatStatus("edit");
        }
        if(headId == null) {
            activityHeadMapper.saveActivityHead(activityHead);
        }else {
            activityHeadMapper.updateActiveHead(activityHead);
        }
        return activityHead.getHeadId().intValue();
    }

    @Override
    public ActivityHead findById(String headId) {
        return activityHeadMapper.findById(headId);
    }
}