package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.thrift.ThriftClient;
import com.linksteady.common.util.ArithUtil;
import com.linksteady.common.util.DateUtil;
import com.linksteady.operate.dao.*;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.domain.enums.ActivityPlanTypeEnum;
import com.linksteady.operate.service.*;
import com.linksteady.operate.vo.ActivityGroupVO;
import com.linksteady.operate.vo.ActivityPlanEffectVO;
import com.linksteady.operate.vo.SmsStatisVO;
import com.linksteady.smp.starter.lognotice.service.ExceptionNoticeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2019-11-02
 */
@Service
@Slf4j
public class QywxActivityPlanServiceImpl implements QywxActivityPlanService {

    @Autowired
    private QywxActivityHeadService qywxActivityHeadService;

    @Autowired
    private QywxActivityPlanMapper qywxActivityPlanMapper;

    @Autowired
   private ConfigService configService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    private final int MAX_TASK_DAY = 7;

    @Autowired
    QywxActivityProductMapper qywxActivityProductMapper;

    @Autowired
    private ThriftClient thriftClient;

    /**
     * ??????thrift ??? seqid???????????????????????????????????????????????????????????????????????????????????????
     * ????????????msg.seqid ???= seqid  => badseqid exception??????????????????
     */
    private ReentrantLock lock = new ReentrantLock();

    /**
     * ??????plan??????
     * @param headId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePlanList(Long headId, String type) {

        //????????????????????????
        qywxActivityHeadService.updateStatus(headId, "todo", type);

        List<QywxActivityPlan> planList = Lists.newArrayList();
        ActivityHead activityHead =qywxActivityHeadService.findById(headId);

        //??????????????????
        String formalStartDt = activityHead.getFormalStartDt();

        //??????????????????
        String formalEndDt = activityHead.getFormalEndDt();

        //??????????????????
        String formalNotifyDt = activityHead.getFormalNotifyDt();

        //???????????????????????????
        if(ActivityPlanTypeEnum.Notify.getPlanTypeCode().equalsIgnoreCase(type)) {
            planList.add(new QywxActivityPlan(Long.valueOf(headId),
                    DateUtil.strToLocalDate(formalNotifyDt,"yyyy-MM-dd"),
                    ActivityPlanTypeEnum.Notify.getPlanTypeCode()));
        }else if(ActivityPlanTypeEnum.During.getPlanTypeCode().equalsIgnoreCase(type)) {
            LocalDate formalStart = DateUtil.strToLocalDate(formalStartDt,"yyyy-MM-dd");
            LocalDate formalEnd = DateUtil.strToLocalDate(formalEndDt,"yyyy-MM-dd");
            //?????????????????????
            while(formalStart.isBefore(formalEnd)) {
                planList.add(new QywxActivityPlan(Long.valueOf(headId),
                        formalStart,
                        ActivityPlanTypeEnum.During.getPlanTypeCode()));

                formalStart = formalStart.plusDays(1);
            }
            planList.add(new QywxActivityPlan(Long.valueOf(headId),
                    formalEnd,
                    ActivityPlanTypeEnum.During.getPlanTypeCode()));
        }
        if(planList.size()>0)
        {
            qywxActivityPlanMapper.savePlanList(planList);
        }
    }

    @Override
    public List<QywxActivityPlan> getPlanList(Long headId) {
        return qywxActivityPlanMapper.getPlanList(headId);
    }

    @Override
    public List<ActivityGroupVO> getPlanGroupList(Long planId) {
        return qywxActivityPlanMapper.getPlanGroupList(planId);
    }

    @Override
    public QywxActivityPlan getPlanInfo(Long planId) {
        return qywxActivityPlanMapper.getPlanInfo(planId);
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
        ActivityPlanEffect activityPlanEffect=qywxActivityPlanMapper.selectPlanEffect(planId);
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
        Long planDt = qywxActivityPlanMapper.getPlanInfo(planId).getPlanDateWid();
        LocalDate taskDtDate = LocalDate.parse(String.valueOf(planDt), DateTimeFormatter.ofPattern(dateFormat));

        // ???????????????????????????(?????????+7???)
        LocalDate maxDate = taskDtDate.plusDays(MAX_TASK_DAY);

        while (taskDtDate.isBefore(maxDate)) {
            xdatas.add(taskDtDate.format(DateTimeFormatter.ofPattern(dateFormat)));
            taskDtDate = taskDtDate.plusDays(1);
        }

        //???????????????????????????
        List<ActivityPlanEffect> dataList = qywxActivityPlanMapper.getPlanEffectStatisList(planId);

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
        return qywxActivityPlanMapper.getPersonalPlanEffect(limit,offset,planId);
    }

    @Override
    public int getDailyPersonalEffectCount(Long planId) {
        return qywxActivityPlanMapper.getDailyPersonalEffectCount(planId);
    }

    @Override
    public void expireActivityPlan() {
        qywxActivityPlanMapper.expireActivityPlan();
    }

    @Override
    public String getPlanStatus(String headId) {
        return qywxActivityPlanMapper.getPlanStatus(headId);
    }

    @Override
    public long calculationList(Long headId, Long planId) throws TException {
        long result=0;
        lock.lock();
        try {
            if (!thriftClient.isOpend()) {
                thriftClient.open();
            }
            result=thriftClient.getInsightService().genActivityPlanDetail(headId,planId);
        }catch (Exception e){
            log.error("??????????????????", e);

            thriftClient.close();
          throw e;
        } finally {
            lock.unlock();
        }
        return result;
    }

    @Override
    public boolean getTransLock(Long headId) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //??????key??????????????????key???????????????value???????????????true. ??????key???????????????????????????????????????false.
        boolean flag = valueOperations.setIfAbsent("calculation_lock", headId, 60, TimeUnit.SECONDS);
        return flag;
    }

    @Override
    public void delTransLock() {
        redisTemplate.delete("calculation_lock");
    }
}
