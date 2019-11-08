package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.linksteady.operate.dao.ActivityHeadMapper;
import com.linksteady.operate.dao.ActivityUserGroupMapper;
import com.linksteady.operate.domain.ActivityGroup;
import com.linksteady.operate.domain.ActivityHead;
import com.linksteady.operate.domain.ActivityPlan;
import com.linksteady.operate.domain.ActivityTemplate;
import com.linksteady.operate.service.ActivityHeadService;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
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
    private ActivityUserGroupMapper activityUserGroupMapper;

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
        if("0".equalsIgnoreCase(activityHead.getHasPreheat())) {
            activityHead.setPreheatStartDt(null);
            activityHead.setPreheatEndDt(null);
        }
        if (headId == null) {
            activityHeadMapper.saveActivityHead(activityHead);

            //更新当前活动是大型活动还是小型活动的标记
            activityHeadMapper.updateActivityFlag(activityHead.getHeadId().toString());

            // 保存群组的初始化信息
            saveGroupData(activityHead.getHeadId().toString(), activityHead.getHasPreheat());
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

    /**
     * @param headId
     * @param stage
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void submitActivity(String headId, String stage) {
        StringBuffer sb = new StringBuffer();
        sb.append("update UO_OP_ACTIVITY_HEADER set ");
        if (stage.equalsIgnoreCase(STAGE_PREHEAT)) {
            sb.append("PREHEAT_STATUS = 'todo'");
        }
        if (stage.equalsIgnoreCase(STAGE_FORMAL)) {
            sb.append("FORMAL_STATUS = 'todo'");
        }
        sb.append(" where head_id = '" + headId + "'");
        activityHeadMapper.submitActivity(sb.toString());
    }

    @Override
    public Map<String, String> getDataChangedStatus(String headId, String stage) {
        return activityHeadMapper.getDataChangedStatus(headId, stage);
    }

    /**
     * 保存群组的初始化信息
     * @param headId
     * @param hasPreheat
     */
    @Transactional(rollbackFor = Exception.class)
    void saveGroupData(String headId, String hasPreheat) {
        List<ActivityGroup> dataList = Lists.newArrayList();

        dataList.add(new ActivityGroup(1L,Long.valueOf(headId),"成长用户","在","活跃"));
        dataList.add(new ActivityGroup(2L,Long.valueOf(headId),"成长用户","在","留存"));
        dataList.add(new ActivityGroup(3L,Long.valueOf(headId),"成长用户","在","流失预警"));
        dataList.add(new ActivityGroup(4L,Long.valueOf(headId),"成长用户","不在",""));

        dataList.add(new ActivityGroup(5L,Long.valueOf(headId),"潜在用户","在","活跃"));
        dataList.add(new ActivityGroup(6L,Long.valueOf(headId),"潜在用户","在","留存"));
        dataList.add(new ActivityGroup(7L,Long.valueOf(headId),"潜在用户","在","流失预警"));

        dataList.add(new ActivityGroup(8L,Long.valueOf(headId),"潜在用户","不在",""));

        //预售
        List<ActivityGroup> preheatList = Lists.newArrayList();
        //正式
        List<ActivityGroup> formalList = Lists.newArrayList();
        dataList.stream().forEach(x->{
            try {
                preheatList.add((ActivityGroup) x.clone());
                formalList.add((ActivityGroup) x.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        });
        //正式 设置阶段标记
        formalList.stream().forEach(x->{
            x.setActivityStage("formal");
        });
        //如果包含预售 再写一份预售的数据
        if("1".equalsIgnoreCase(hasPreheat)) {
            preheatList.stream().forEach(x->{
                x.setActivityStage("preheat");
            });
            formalList.addAll(preheatList);
        }
        activityUserGroupMapper.saveGroupData(formalList);
        savePlanList(headId, hasPreheat);
    }

    /**
     * 生成plan数据
     * @param headId
     * @param hasPreheat
     */
    @Transactional(rollbackFor = Exception.class)
    void savePlanList(String headId, String hasPreheat) {
        List<ActivityPlan> planList = Lists.newArrayList();
        Map<String, Date> dateMap = activityHeadMapper.getStageDate(headId);
        Date formalStartDt = dateMap.get("FORMAL_START_DT");
        Date formalEndDt = dateMap.get("FORMAL_END_DT");
        Date preheatStartDt = dateMap.get("PREHEAT_START_DT");
        Date preheatEndDt = dateMap.get("PREHEAT_END_DT");
        // 不包含预热

        LocalDate formalStart = dateToLocalDate(formalStartDt);
        LocalDate formalEnd = dateToLocalDate(formalEndDt);
        while(formalStart.isBefore(formalEnd)) {
            planList.add(new ActivityPlan(Long.valueOf(headId), 0L, localDateToDate(formalStart), "todo", "formal"));
            formalStart = formalStart.plusDays(1);
        }
        planList.add(new ActivityPlan(Long.valueOf(headId), 0L, localDateToDate(formalEnd), "todo", "formal"));

        // 包含预热
        if("1".equalsIgnoreCase(hasPreheat)) {
            LocalDate start = dateToLocalDate(preheatStartDt);
            LocalDate end = dateToLocalDate(preheatEndDt);
            while(start.isBefore(end)) {
                planList.add(new ActivityPlan(Long.valueOf(headId), 0L, localDateToDate(start), "todo", "preheat"));
                start = start.plusDays(1);
            }
            planList.add(new ActivityPlan(Long.valueOf(headId), 0L, localDateToDate(end), "todo", "preheat"));
        }
        activityHeadMapper.savePlanList(planList);
    }

    private LocalDate dateToLocalDate(Date date){
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone).toLocalDate();
    }

    private Date localDateToDate(LocalDate date){
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = date.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }
}