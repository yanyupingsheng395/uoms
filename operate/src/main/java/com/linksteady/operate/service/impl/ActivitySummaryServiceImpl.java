package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.linksteady.common.util.DateUtil;
import com.linksteady.operate.dao.ActivityHeadMapper;
import com.linksteady.operate.dao.ActivitySummaryMapper;
import com.linksteady.operate.domain.ActivitySummary;
import com.linksteady.operate.service.ActivitySummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ActivitySummaryServiceImpl implements ActivitySummaryService {

    @Autowired
    private ActivitySummaryMapper activitySummaryMapper;

    @Autowired
    private ActivityHeadMapper activityHeadMapper;

    @Override
    public List<ActivitySummary> getUserGroupList(String headId, String planDtWid) {
        return activitySummaryMapper.getUserGroupList(headId, planDtWid);
    }

    /**
     * 更新计划的统计表信息
     * @param headId
     * @param hasPreheat
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSummaryList(String headId, String hasPreheat) {
        List<ActivitySummary> dataList = Lists.newArrayList();
        Map<String, Date> dateMap = activityHeadMapper.getStageDate(headId);
        Date formalStartDt = dateMap.get("FORMAL_START_DT");
        Date formalEndDt = dateMap.get("FORMAL_END_DT");
        Date preheatStartDt = dateMap.get("PREHEAT_START_DT");
        Date preheatEndDt = dateMap.get("PREHEAT_END_DT");
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
        // 不包含预热
        List<ActivitySummary> formalList = Lists.newArrayList();
        LocalDate formalStart = DateUtil.dateToLocalDate(formalStartDt);
        LocalDate formalEnd = DateUtil.dateToLocalDate(formalEndDt);
        while(formalStart.isBefore(formalEnd)) {
            dataList.add(new ActivitySummary(1L,Long.valueOf(headId),"成长用户","在","活跃", Long.parseLong(formatters.format(formalStart))));
            dataList.add(new ActivitySummary(2L,Long.valueOf(headId),"成长用户","在","留存", Long.parseLong(formatters.format(formalStart))));
            dataList.add(new ActivitySummary(3L,Long.valueOf(headId),"成长用户","在","流失预警", Long.parseLong(formatters.format(formalStart))));
            dataList.add(new ActivitySummary(4L,Long.valueOf(headId),"成长用户","不在","", Long.parseLong(formatters.format(formalStart))));
            dataList.add(new ActivitySummary(5L,Long.valueOf(headId),"潜在用户","在","活跃", Long.parseLong(formatters.format(formalStart))));
            dataList.add(new ActivitySummary(6L,Long.valueOf(headId),"潜在用户","在","留存", Long.parseLong(formatters.format(formalStart))));
            dataList.add(new ActivitySummary(7L,Long.valueOf(headId),"潜在用户","在","流失预警", Long.parseLong(formatters.format(formalStart))));
            dataList.add(new ActivitySummary(8L,Long.valueOf(headId),"潜在用户","不在","", Long.parseLong(formatters.format(formalStart))));
            formalStart = formalStart.plusDays(1);
        }
        dataList.add(new ActivitySummary(1L,Long.valueOf(headId),"成长用户","在","活跃", Long.parseLong(formatters.format(formalEnd))));
        dataList.add(new ActivitySummary(2L,Long.valueOf(headId),"成长用户","在","留存", Long.parseLong(formatters.format(formalEnd))));
        dataList.add(new ActivitySummary(3L,Long.valueOf(headId),"成长用户","在","流失预警", Long.parseLong(formatters.format(formalEnd))));
        dataList.add(new ActivitySummary(4L,Long.valueOf(headId),"成长用户","不在","", Long.parseLong(formatters.format(formalEnd))));
        dataList.add(new ActivitySummary(5L,Long.valueOf(headId),"潜在用户","在","活跃", Long.parseLong(formatters.format(formalEnd))));
        dataList.add(new ActivitySummary(6L,Long.valueOf(headId),"潜在用户","在","留存", Long.parseLong(formatters.format(formalEnd))));
        dataList.add(new ActivitySummary(7L,Long.valueOf(headId),"潜在用户","在","流失预警", Long.parseLong(formatters.format(formalEnd))));
        dataList.add(new ActivitySummary(8L,Long.valueOf(headId),"潜在用户","不在","", Long.parseLong(formatters.format(formalEnd))));

        // 包含预热
        if("1".equalsIgnoreCase(hasPreheat)) {
            LocalDate start = DateUtil.dateToLocalDate(preheatStartDt);
            LocalDate end = DateUtil.dateToLocalDate(preheatEndDt);
            while(start.isBefore(end)) {
                dataList.add(new ActivitySummary(1L,Long.valueOf(headId),"成长用户","在","活跃", Long.parseLong(formatters.format(start))));
                dataList.add(new ActivitySummary(2L,Long.valueOf(headId),"成长用户","在","留存", Long.parseLong(formatters.format(start))));
                dataList.add(new ActivitySummary(3L,Long.valueOf(headId),"成长用户","在","流失预警", Long.parseLong(formatters.format(start))));
                dataList.add(new ActivitySummary(4L,Long.valueOf(headId),"成长用户","不在","", Long.parseLong(formatters.format(start))));
                dataList.add(new ActivitySummary(5L,Long.valueOf(headId),"潜在用户","在","活跃", Long.parseLong(formatters.format(start))));
                dataList.add(new ActivitySummary(6L,Long.valueOf(headId),"潜在用户","在","留存", Long.parseLong(formatters.format(start))));
                dataList.add(new ActivitySummary(7L,Long.valueOf(headId),"潜在用户","在","流失预警", Long.parseLong(formatters.format(start))));
                dataList.add(new ActivitySummary(8L,Long.valueOf(headId),"潜在用户","不在","", Long.parseLong(formatters.format(start))));
                start = start.plusDays(1);
            }
            dataList.add(new ActivitySummary(1L,Long.valueOf(headId),"成长用户","在","活跃", Long.parseLong(formatters.format(end))));
            dataList.add(new ActivitySummary(2L,Long.valueOf(headId),"成长用户","在","留存", Long.parseLong(formatters.format(end))));
            dataList.add(new ActivitySummary(3L,Long.valueOf(headId),"成长用户","在","流失预警", Long.parseLong(formatters.format(end))));
            dataList.add(new ActivitySummary(4L,Long.valueOf(headId),"成长用户","不在","", Long.parseLong(formatters.format(end))));
            dataList.add(new ActivitySummary(5L,Long.valueOf(headId),"潜在用户","在","活跃", Long.parseLong(formatters.format(end))));
            dataList.add(new ActivitySummary(6L,Long.valueOf(headId),"潜在用户","在","留存", Long.parseLong(formatters.format(end))));
            dataList.add(new ActivitySummary(7L,Long.valueOf(headId),"潜在用户","在","流失预警", Long.parseLong(formatters.format(end))));
            dataList.add(new ActivitySummary(8L,Long.valueOf(headId),"潜在用户","不在","", Long.parseLong(formatters.format(end))));
        }
        activitySummaryMapper.saveActivitySummaryList(dataList);
    }
}
