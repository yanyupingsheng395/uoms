package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.util.ArithUtil;
import com.linksteady.common.util.DateUtil;
import com.linksteady.operate.dao.ActivityHeadMapper;
import com.linksteady.operate.dao.ActivityPlanMapper;
import com.linksteady.operate.dao.ActivityProductMapper;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.domain.enums.ActivityPlanTypeEnum;
import com.linksteady.operate.domain.enums.ActivityStageEnum;
import com.linksteady.operate.service.ActivityHeadService;
import com.linksteady.operate.service.ActivityPlanService;
import com.linksteady.operate.service.ShortUrlService;
import com.linksteady.operate.vo.ActivityGroupVO;
import com.linksteady.operate.vo.ActivityPlanEffectVO;
import com.linksteady.operate.vo.SmsStatisVO;
import com.linksteady.smp.starter.lognotice.service.ExceptionNoticeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
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
     * ??????plan??????
     * @param headId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePlanList(Long headId, String stage, String type) {

        //????????????????????????
        activityHeadService.updateStatus(headId, stage, "todo", type);

        List<ActivityPlan> planList = Lists.newArrayList();
        ActivityHead activityHead =activityHeadMapper.findById(headId);

        //??????????????????
        String formalStartDt = activityHead.getFormalStartDt();

        //??????????????????
        String formalEndDt = activityHead.getFormalEndDt();
        //??????????????????
        String preheatStartDt = activityHead.getPreheatStartDt();
        //??????????????????
        String preheatEndDt = activityHead.getPreheatEndDt();

        //??????????????????
        String preheatNotifyDt =activityHead.getPreheatNotifyDt();
        //??????????????????
        String formalNotifyDt = activityHead.getFormalNotifyDt();

        if(stage.equals(ActivityStageEnum.formal.getStageCode())) {
            //???????????????????????????
            if(ActivityPlanTypeEnum.Notify.getPlanTypeCode().equalsIgnoreCase(type)) {
                planList.add(new ActivityPlan(Long.valueOf(headId),
                        DateUtil.strToLocalDate(formalNotifyDt,"yyyy-MM-dd"),
                        ActivityStageEnum.formal.getStageCode(),
                        ActivityPlanTypeEnum.Notify.getPlanTypeCode()));
            }else if(ActivityPlanTypeEnum.During.getPlanTypeCode().equalsIgnoreCase(type)) {
                LocalDate formalStart = DateUtil.strToLocalDate(formalStartDt,"yyyy-MM-dd");
                LocalDate formalEnd = DateUtil.strToLocalDate(formalEndDt,"yyyy-MM-dd");
                //?????????????????????
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

        // ????????????
        if(stage.equalsIgnoreCase(ActivityStageEnum.preheat.getStageCode())) {
            if(ActivityPlanTypeEnum.Notify.getPlanTypeCode().equalsIgnoreCase(type)) {
                //???????????????????????????
                planList.add(new ActivityPlan(Long.valueOf(headId),
                        DateUtil.strToLocalDate(preheatNotifyDt,"yyyy-MM-dd"),
                        ActivityStageEnum.preheat.getStageCode(),
                        ActivityPlanTypeEnum.Notify.getPlanTypeCode()));
            }else if(ActivityPlanTypeEnum.During.getPlanTypeCode().equalsIgnoreCase(type)) {
                LocalDate start = DateUtil.strToLocalDate(preheatStartDt,"yyyy-MM-dd");
                LocalDate end = DateUtil.strToLocalDate(preheatEndDt,"yyyy-MM-dd");
                //?????????????????????
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
    public List<ActivityGroupVO> getPlanGroupList(Long planId) {
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

        //???????????????????????????
        String price=configService.getValueByName("op.push.smsPrice");

        double smsPrice;
        if(StringUtils.isEmpty(price))
        {
            smsPrice=0.042d;
        }else
        {
             smsPrice=Double.parseDouble(price);
        }


        //??????????????????
        ActivityPlanEffect activityPlanEffect=activityPlanMapper.selectPlanEffect(planId);
        //?????????  ?????????????????? ????????????  ????????????  ??????SPU????????????  ??????SPU????????????

        ActivityPlanEffectVO activityPlanEffectVO=new ActivityPlanEffectVO();
        activityPlanEffectVO.setUserCount(activityPlanEffect.getUserCount());
        activityPlanEffectVO.setSuccessCount(activityPlanEffect.getSuccessCount());

        //????????????
        activityPlanEffectVO.setPushCost(smsPrice*activityPlanEffect.getSuccessCount());
        //???????????????????????????????????????
        if("1".equals(kpiType))
        {
            //????????????
            activityPlanEffectVO.setCovUserCount(activityPlanEffect.getSpuUserCount());
            //????????????
            activityPlanEffectVO.setCovAmount(activityPlanEffect.getSpuAmount());

            //???????????????
            if(activityPlanEffect.getSuccessCount()==0L)
            {
                activityPlanEffectVO.setCovRate(0D);
            }else
            {
                activityPlanEffectVO.setCovRate(ArithUtil.formatDoubleByMode((double)activityPlanEffect.getSpuUserCount()/(double)activityPlanEffect.getSuccessCount()*100,2, RoundingMode.HALF_UP));
            }

            //???????????????????????????  ??????/??????
            if(activityPlanEffect.getSuccessCount()==0L)
            {
                activityPlanEffectVO.setPushPerIncome(0D);
            }else
            {
                activityPlanEffectVO.setPushPerIncome(ArithUtil.formatDoubleByMode((double)activityPlanEffect.getSpuAmount()/((double)activityPlanEffect.getSuccessCount()*smsPrice),2, RoundingMode.HALF_UP));
            }

        }else
        {
            //????????????
            activityPlanEffectVO.setCovUserCount(activityPlanEffect.getCovUserCount());
            //????????????
            activityPlanEffectVO.setCovAmount(activityPlanEffect.getCovAmount());
            //?????????
            //???????????????
            if(activityPlanEffect.getSuccessCount()==0L)
            {
                activityPlanEffectVO.setCovRate(0D);
            }else
            {
                activityPlanEffectVO.setCovRate(ArithUtil.formatDoubleByMode((double)activityPlanEffect.getCovUserCount()/(double)activityPlanEffect.getSuccessCount()*100,2, RoundingMode.HALF_UP));
            }

            //???????????????????????????  ??????/??????
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

        // ??????????????????
        Long planDt = activityPlanMapper.getPlanInfo(planId).getPlanDateWid();
        LocalDate taskDtDate = LocalDate.parse(String.valueOf(planDt), DateTimeFormatter.ofPattern(dateFormat));

        // ???????????????????????????(?????????+7???)
        LocalDate maxDate = taskDtDate.plusDays(MAX_TASK_DAY);

        while (taskDtDate.isBefore(maxDate)) {
            xdatas.add(taskDtDate.format(DateTimeFormatter.ofPattern(dateFormat)));
            taskDtDate = taskDtDate.plusDays(1);
        }

        //???????????????????????????
        List<ActivityPlanEffect> dataList = activityPlanMapper.getPlanEffectStatisList(planId);

        Map<String,ActivityPlanEffect> dataMap=dataList.stream().collect(Collectors.toMap(ActivityPlanEffect::getConversionDate,a->a));

        //????????????
        List<Long> covNumList=Lists.newArrayList();
        //?????????????????????????????????
        List<Long> covSpuNumList=Lists.newArrayList();
        //?????????
        List<Double> covRateList=Lists.newArrayList();
        //SPU?????????
        List<Double> covSpuRateList=Lists.newArrayList();

        xdatas.forEach(x -> {
            if(null!=dataMap.get(x))
            {
                ActivityPlanEffect activityPlanEffect=dataMap.get(x);
                covNumList.add(activityPlanEffect.getCovUserCount());
                covSpuNumList.add(activityPlanEffect.getSpuUserCount());
                //???????????????
                if(activityPlanEffect.getSuccessCount()==0L)
                {
                    covRateList.add(0D);
                }else
                {
                    covSpuRateList.add(ArithUtil.formatDoubleByMode((double)activityPlanEffect.getCovUserCount()/(double)activityPlanEffect.getSuccessCount()*100,2, RoundingMode.DOWN));
                }

                //?????????????????????????????????
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
