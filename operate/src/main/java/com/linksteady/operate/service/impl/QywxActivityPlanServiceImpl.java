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
     * 由于thrift 中 seqid资源是线程不安全的，所以需要通过加锁的方式来同步调用资源。
     * 否则会报msg.seqid ！= seqid  => badseqid exception的异常信息。
     */
    private ReentrantLock lock = new ReentrantLock();

    /**
     * 生成plan数据
     * @param headId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePlanList(Long headId, String type) {

        //更新活动头的状态
        qywxActivityHeadService.updateStatus(headId, "todo", type);

        List<QywxActivityPlan> planList = Lists.newArrayList();
        ActivityHead activityHead =qywxActivityHeadService.findById(headId);

        //正式开始时间
        String formalStartDt = activityHead.getFormalStartDt();

        //正式结束时间
        String formalEndDt = activityHead.getFormalEndDt();

        //正式提醒时间
        String formalNotifyDt = activityHead.getFormalNotifyDt();

        //写入正式的提醒记录
        if(ActivityPlanTypeEnum.Notify.getPlanTypeCode().equalsIgnoreCase(type)) {
            planList.add(new QywxActivityPlan(Long.valueOf(headId),
                    DateUtil.strToLocalDate(formalNotifyDt,"yyyy-MM-dd"),
                    ActivityPlanTypeEnum.Notify.getPlanTypeCode()));
        }else if(ActivityPlanTypeEnum.During.getPlanTypeCode().equalsIgnoreCase(type)) {
            LocalDate formalStart = DateUtil.strToLocalDate(formalStartDt,"yyyy-MM-dd");
            LocalDate formalEnd = DateUtil.strToLocalDate(formalEndDt,"yyyy-MM-dd");
            //写入正式的记录
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
        ActivityPlanEffect activityPlanEffect=qywxActivityPlanMapper.selectPlanEffect(planId);
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
        Long planDt = qywxActivityPlanMapper.getPlanInfo(planId).getPlanDateWid();
        LocalDate taskDtDate = LocalDate.parse(String.valueOf(planDt), DateTimeFormatter.ofPattern(dateFormat));

        // 横轴显示的最多天数(活动后+7天)
        LocalDate maxDate = taskDtDate.plusDays(MAX_TASK_DAY);

        while (taskDtDate.isBefore(maxDate)) {
            xdatas.add(taskDtDate.format(DateTimeFormatter.ofPattern(dateFormat)));
            taskDtDate = taskDtDate.plusDays(1);
        }

        //获取按天的转化数据
        List<ActivityPlanEffect> dataList = qywxActivityPlanMapper.getPlanEffectStatisList(planId);

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
            log.error("计算名单错误", e);

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
        //如果key不存在，则将key的值设置为value，同时返回true. 如果key不存在，则什么也不做，返回false.
        boolean flag = valueOperations.setIfAbsent("calculation_lock", headId, 60, TimeUnit.SECONDS);
        return flag;
    }

    @Override
    public void delTransLock() {
        redisTemplate.delete("calculation_lock");
    }
}
