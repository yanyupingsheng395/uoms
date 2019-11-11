package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.linksteady.common.util.DateUtil;
import com.linksteady.operate.dao.ActivityHeadMapper;
import com.linksteady.operate.dao.ActivityPlanMapper;
import com.linksteady.operate.dao.ActivitySummaryMapper;
import com.linksteady.operate.dao.ActivityUserGroupMapper;
import com.linksteady.operate.domain.ActivityGroup;
import com.linksteady.operate.domain.ActivityPlan;
import com.linksteady.operate.domain.ActivitySummary;
import com.linksteady.operate.service.ActivityPlanService;
import com.linksteady.operate.service.ActivityUserGroupService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2019-11-02
 */
@Service
public class ActivityPlanServiceImpl implements ActivityPlanService {

    @Autowired
    private ActivityHeadMapper activityHeadMapper;

    @Autowired
    private ActivityPlanMapper activityPlanMapper;


    /**
     * 生成plan数据
     * @param headId
     * @param hasPreheat
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePlanList(String headId, String hasPreheat) {
        List<ActivityPlan> planList = Lists.newArrayList();
        Map<String, Date> dateMap = activityHeadMapper.getStageDate(headId);
        Date formalStartDt = dateMap.get("FORMAL_START_DT");
        Date formalEndDt = dateMap.get("FORMAL_END_DT");
        Date preheatStartDt = dateMap.get("PREHEAT_START_DT");
        Date preheatEndDt = dateMap.get("PREHEAT_END_DT");
        // 不包含预热

        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate formalStart = DateUtil.dateToLocalDate(formalStartDt);
        LocalDate formalEnd = DateUtil.dateToLocalDate(formalEndDt);
        while(formalStart.isBefore(formalEnd)) {
            planList.add(new ActivityPlan(Long.valueOf(headId), 0L, DateUtil.localDateToDate(formalStart), "0", "formal",Long.parseLong(formatters.format(formalStart))));
            formalStart = formalStart.plusDays(1);
        }
        planList.add(new ActivityPlan(Long.valueOf(headId), 0L, DateUtil.localDateToDate(formalEnd), "0", "formal",Long.parseLong(formatters.format(formalEnd))));

        // 包含预热
        if("1".equalsIgnoreCase(hasPreheat)) {
            LocalDate start = DateUtil.dateToLocalDate(preheatStartDt);
            LocalDate end = DateUtil.dateToLocalDate(preheatEndDt);
            while(start.isBefore(end)) {
                planList.add(new ActivityPlan(Long.valueOf(headId), 0L, DateUtil.localDateToDate(start), "0", "preheat",Long.parseLong(formatters.format(start))));
                start = start.plusDays(1);
            }
            planList.add(new ActivityPlan(Long.valueOf(headId), 0L, DateUtil.localDateToDate(end), "0", "preheat",Long.parseLong(formatters.format(end))));
        }
        activityPlanMapper.savePlanList(planList);
    }

    @Override
    public List<ActivityPlan> getPlanList(String headId) {
        return activityPlanMapper.getPlanList(headId);
    }
}
