package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.util.ArithUtil;
import com.linksteady.common.util.DateUtil;
import com.linksteady.lognotice.service.ExceptionNoticeHandler;
import com.linksteady.operate.dao.ActivityDetailMapper;
import com.linksteady.operate.dao.ActivityHeadMapper;
import com.linksteady.operate.dao.ActivityPlanMapper;
import com.linksteady.operate.dao.ActivityProductMapper;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.domain.enums.ActivityPlanStatusEnum;
import com.linksteady.operate.domain.enums.ActivityPlanTypeEnum;
import com.linksteady.operate.domain.enums.ActivityStageEnum;
import com.linksteady.operate.domain.enums.ActivityStatusEnum;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.exception.OptimisticLockException;
import com.linksteady.operate.service.ActivityHeadService;
import com.linksteady.operate.service.ActivityPlanService;
import com.linksteady.operate.service.ShortUrlService;
import com.linksteady.operate.thread.TransActivityContentThread;
import com.linksteady.operate.vo.ActivityContentVO;
import com.linksteady.operate.vo.ActivityPlanEffectVO;
import com.linksteady.operate.vo.SmsStatisVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2019-11-02
 */
@Service
@Slf4j
public class ActivityPlanServiceImpl implements ActivityPlanService {

    @Autowired
    private ActivityHeadService activityHeadService;

    @Autowired
    private ActivityHeadMapper activityHeadMapper;

    @Autowired
    private ActivityPlanMapper activityPlanMapper;

    @Autowired
   private ConfigService configService;

    @Autowired
    ShortUrlService shortUrlService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    private final int MAX_TASK_DAY = 7;

    @Autowired
    ActivityProductMapper activityProductMapper;

    /**
     * 生成plan数据
     * @param headId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePlanList(Long headId, String stage, String type) {

        //更新活动头的状态
        activityHeadService.updateStatus(headId, stage, "todo", type);

        List<ActivityPlan> planList = Lists.newArrayList();
        ActivityHead activityHead =activityHeadMapper.findById(headId);

        //正式开始时间
        String formalStartDt = activityHead.getFormalStartDt();

        //正式结束时间
        String formalEndDt = activityHead.getFormalEndDt();
        //预热开始时间
        String preheatStartDt = activityHead.getPreheatStartDt();
        //预热结束时间
        String preheatEndDt = activityHead.getPreheatEndDt();

        //预热提醒时间
        String preheatNotifyDt =activityHead.getPreheatNotifyDt();
        //正式提醒时间
        String formalNotifyDt = activityHead.getFormalNotifyDt();

        if(stage.equals(ActivityStageEnum.formal.getStageCode())) {
            //写入正式的提醒记录
            if(ActivityPlanTypeEnum.Notify.getPlanTypeCode().equalsIgnoreCase(type)) {
                planList.add(new ActivityPlan(Long.valueOf(headId),
                        DateUtil.strToLocalDate(formalNotifyDt,"yyyy-MM-dd"),
                        ActivityStageEnum.formal.getStageCode(),
                        ActivityPlanTypeEnum.Notify.getPlanTypeCode()));
            }else if(ActivityPlanTypeEnum.During.getPlanTypeCode().equalsIgnoreCase(type)) {
                LocalDate formalStart = DateUtil.strToLocalDate(formalStartDt,"yyyy-MM-dd");
                LocalDate formalEnd = DateUtil.strToLocalDate(formalEndDt,"yyyy-MM-dd");
                //写入正式的记录
                while(formalStart.isBefore(formalEnd)) {
                    planList.add(new ActivityPlan(Long.valueOf(headId),
                            formalStart,
                            ActivityStageEnum.formal.getStageCode(),
                            ActivityPlanTypeEnum.During.getPlanTypeCode()));

                    formalStart = formalStart.plusDays(1);
                }
                planList.add(new ActivityPlan(Long.valueOf(headId),
                        formalEnd,
                        ActivityStageEnum.formal.getStageCode(),
                        ActivityPlanTypeEnum.During.getPlanTypeCode()));
            }
        }

        // 包含预售
        if(stage.equalsIgnoreCase(ActivityStageEnum.preheat.getStageCode())) {
            if(ActivityPlanTypeEnum.Notify.getPlanTypeCode().equalsIgnoreCase(type)) {
                //写入预售的提醒记录
                planList.add(new ActivityPlan(Long.valueOf(headId),
                        DateUtil.strToLocalDate(preheatNotifyDt,"yyyy-MM-dd"),
                        ActivityStageEnum.preheat.getStageCode(),
                        ActivityPlanTypeEnum.Notify.getPlanTypeCode()));
            }else if(ActivityPlanTypeEnum.During.getPlanTypeCode().equalsIgnoreCase(type)) {
                LocalDate start = DateUtil.strToLocalDate(preheatStartDt,"yyyy-MM-dd");
                LocalDate end = DateUtil.strToLocalDate(preheatEndDt,"yyyy-MM-dd");
                //写入预售的记录
                while(start.isBefore(end)) {
                    planList.add(new ActivityPlan(Long.valueOf(headId),
                            start,
                            ActivityStageEnum.preheat.getStageCode(),
                            ActivityPlanTypeEnum.During.getPlanTypeCode()));
                    start = start.plusDays(1);
                }
                planList.add(new ActivityPlan(Long.valueOf(headId),
                        end,
                        ActivityStageEnum.preheat.getStageCode(),
                        ActivityPlanTypeEnum.During.getPlanTypeCode()));
            }
        }
        activityPlanMapper.savePlanList(planList);
    }

    @Override
    public List<ActivityPlan> getPlanList(Long headId) {
        return activityPlanMapper.getPlanList(headId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePlan(Long headId) {
        activityPlanMapper.deletePlan(headId);
        activityPlanMapper.deletePlanGroup(headId);
    }

    @Override
    public List<ActivityPlanGroup> getPlanGroupList(Long planId) {
        return activityPlanMapper.getPlanGroupList(planId);
    }

    @Override
    public int getPlanSmsContentListCount(Long planId) {
        return activityPlanMapper.getPlanSmsContentListCount(planId);
    }

    @Override
    public List<SmsStatisVO> getPlanSmsContentList(Long planId, int limit, int offset) {
        return activityPlanMapper.getPlanSmsContentList(planId,limit,offset);
    }

    @Override
    public ActivityPlan getPlanInfo(Long planId) {
        return activityPlanMapper.getPlanInfo(planId);
    }

    @Override
    public ActivityPlanEffectVO getPlanEffectById(Long planId,String kpiType) {

        //获取单条短信的价格
        String price=configService.getValueByName("op.push.smsPrice");

        double smsPrice;
        if(StringUtils.isEmpty(price))
        {
            smsPrice=0.042d;
        }else
        {
             smsPrice=Double.parseDouble(price);
        }


        //获取转化数据
        ActivityPlanEffect activityPlanEffect=activityPlanMapper.selectPlanEffect(planId);
        //总人数  成功推送人数 转化人数  转化金额  购买SPU转化人数  购买SPU转化金额

        ActivityPlanEffectVO activityPlanEffectVO=new ActivityPlanEffectVO();
        activityPlanEffectVO.setUserCount(activityPlanEffect.getUserCount());
        activityPlanEffectVO.setSuccessCount(activityPlanEffect.getSuccessCount());

        //推送成本
        activityPlanEffectVO.setPushCost(smsPrice*activityPlanEffect.getSuccessCount());
        //推送且购买推荐类目转化人数
        if("1".equals(kpiType))
        {
            //转化人数
            activityPlanEffectVO.setCovUserCount(activityPlanEffect.getSpuUserCount());
            //转化金额
            activityPlanEffectVO.setCovAmount(activityPlanEffect.getSpuAmount());

            //计算转化率
            if(activityPlanEffect.getSuccessCount()==0L)
            {
                activityPlanEffectVO.setCovRate(0D);
            }else
            {
                activityPlanEffectVO.setCovRate(ArithUtil.formatDoubleByMode((double)activityPlanEffect.getSpuUserCount()/(double)activityPlanEffect.getSuccessCount()*100,2, RoundingMode.HALF_UP));
            }

            //每推送成本带来收入  收入/成本
            if(activityPlanEffect.getSuccessCount()==0L)
            {
                activityPlanEffectVO.setPushPerIncome(0D);
            }else
            {
                activityPlanEffectVO.setPushPerIncome(ArithUtil.formatDoubleByMode((double)activityPlanEffect.getSpuAmount()/((double)activityPlanEffect.getSuccessCount()*smsPrice),2, RoundingMode.HALF_UP));
            }

        }else
        {
            //转化人数
            activityPlanEffectVO.setCovUserCount(activityPlanEffect.getCovUserCount());
            //转化金额
            activityPlanEffectVO.setCovAmount(activityPlanEffect.getCovAmount());
            //转化率
            //计算转化率
            if(activityPlanEffect.getSuccessCount()==0L)
            {
                activityPlanEffectVO.setCovRate(0D);
            }else
            {
                activityPlanEffectVO.setCovRate(ArithUtil.formatDoubleByMode((double)activityPlanEffect.getCovUserCount()/(double)activityPlanEffect.getSuccessCount()*100,2, RoundingMode.HALF_UP));
            }

            //每推送成本带来收入  收入/成本
            if(activityPlanEffect.getSuccessCount()==0L)
            {
                activityPlanEffectVO.setPushPerIncome(0D);
            }else
            {
                activityPlanEffectVO.setPushPerIncome(ArithUtil.formatDoubleByMode((double)activityPlanEffect.getCovAmount()/((double)activityPlanEffect.getSuccessCount()*smsPrice),2, RoundingMode.HALF_UP));
            }

        }

        return activityPlanEffectVO;
    }

    @Override
    public Map<String, Object> getPlanEffectTrend(Long planId) {
        Map<String, Object> result = Maps.newHashMap();
        String dateFormat = "yyyyMMdd";
        List<String> xdatas = Lists.newLinkedList();

        // 提交任务日期
        Long planDt = activityPlanMapper.getPlanInfo(planId).getPlanDateWid();
        LocalDate taskDtDate = LocalDate.parse(String.valueOf(planDt), DateTimeFormatter.ofPattern(dateFormat));

        // 横轴显示的最多天数(活动后+7天)
        LocalDate maxDate = taskDtDate.plusDays(MAX_TASK_DAY);

        while (taskDtDate.isBefore(maxDate)) {
            xdatas.add(taskDtDate.format(DateTimeFormatter.ofPattern(dateFormat)));
            taskDtDate = taskDtDate.plusDays(1);
        }

        //获取按天的转化数据
        List<ActivityPlanEffect> dataList = activityPlanMapper.getPlanEffectStatisList(planId);

        Map<String,ActivityPlanEffect> dataMap=dataList.stream().collect(Collectors.toMap(ActivityPlanEffect::getConversionDate,a->a));

        //转化人数
        List<Long> covNumList=Lists.newArrayList();
        //转化且购买推荐类目人数
        List<Long> covSpuNumList=Lists.newArrayList();
        //转化率
        List<Double> covRateList=Lists.newArrayList();
        //SPU转化率
        List<Double> covSpuRateList=Lists.newArrayList();

        xdatas.forEach(x -> {
            if(null!=dataMap.get(x))
            {
                ActivityPlanEffect activityPlanEffect=dataMap.get(x);
                covNumList.add(activityPlanEffect.getCovUserCount());
                covSpuNumList.add(activityPlanEffect.getSpuUserCount());
                //计算转化率
                if(activityPlanEffect.getSuccessCount()==0L)
                {
                    covRateList.add(0D);
                }else
                {
                    covSpuRateList.add(ArithUtil.formatDoubleByMode((double)activityPlanEffect.getCovUserCount()/(double)activityPlanEffect.getSuccessCount()*100,2, RoundingMode.DOWN));
                }

                //计算购买推荐类目转化率
                if(activityPlanEffect.getSuccessCount()==0L)
                {
                    covSpuRateList.add(0D);
                }else
                {
                    covSpuRateList.add(ArithUtil.formatDoubleByMode((double)activityPlanEffect.getSpuUserCount()/(double)activityPlanEffect.getSuccessCount()*100,2, RoundingMode.DOWN));
                }
            }else
            {
                covNumList.add(0L);
                covSpuNumList.add(0L);
                covRateList.add(0D);
                covSpuRateList.add(0D);
            }
        });

        result.put("xdata", xdatas);
        result.put("ydata1", covNumList);
        result.put("ydata2", covSpuNumList);
        result.put("ydata3", covRateList);
        result.put("ydata4", covSpuRateList);
        return result;
    }

    @Override
    public List<ActivityPersonal> getPersonalPlanEffect(int limit, int offset, Long planId) {
        return activityPlanMapper.getPersonalPlanEffect(limit,offset,planId);
    }

    @Override
    public int getDailyPersonalEffectCount(Long planId) {
        return activityPlanMapper.getDailyPersonalEffectCount(planId);
    }

    @Override
    public void expireActivityPlan() {
        activityPlanMapper.expireActivityPlan();
    }

    @Override
    public String getPlanStatus(String headId, String stage) {
        return activityPlanMapper.getPlanStatus(headId, stage);
    }
}
