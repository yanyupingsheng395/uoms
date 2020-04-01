package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.util.ArithUtil;
import com.linksteady.common.util.DateUtil;
import com.linksteady.lognotice.service.ExceptionNoticeHandler;
import com.linksteady.operate.dao.*;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.domain.enums.ActivityPlanStatusEnum;
import com.linksteady.operate.domain.enums.ActivityPlanTypeEnum;
import com.linksteady.operate.domain.enums.ActivityStageEnum;
import com.linksteady.operate.domain.enums.ActivityStatusEnum;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.exception.OptimisticLockException;
import com.linksteady.operate.service.ActivityHeadService;
import com.linksteady.operate.service.ActivityPlanService;
import com.linksteady.operate.service.ActivityPushService;
import com.linksteady.operate.service.ShortUrlService;
import com.linksteady.operate.thread.TransActivityContentThread;
import com.linksteady.operate.util.SpringContextUtils;
import com.linksteady.operate.vo.ActivityContentVO;
import com.linksteady.operate.vo.ActivityPlanEffectVO;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author huangkun
 * @date 2020-03-31
 * 活动运营转化文案、推送、停止推送
 */
@Service
@Slf4j
public class ActivityPushServiceImpl implements ActivityPushService {

    @Autowired
    private ActivityHeadService activityHeadService;

    @Autowired
    private ActivityDetailMapper activityDetailMapper;

    @Autowired
    private ActivityPushMapper activityPushMapper;

    @Autowired
    ShortUrlService shortUrlService;

    @Autowired
    private PushProperties pushProperties;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @Autowired
    ActivityProductMapper activityProductMapper;


    /**
     * 对活动推送文案进行转换
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transActivityDetail( ActivityPlan activityPlan) throws Exception {
        //获取锁
        if(getTransActivityContentLock(activityPlan.getHeadId()))
        {
            initPlanGroup(activityPlan);

            //删除临时表中的文案
            activityPushMapper.deleteContentTmp(activityPlan.getPlanId());
            transContent(activityPlan.getHeadId(),activityPlan.getPlanId(),activityPlan.getStage(),activityPlan.getPlanType());
             //释放锁
            delTransLock();
        }else
        {
            throw new OptimisticLockException("其他用户正在操作，请稍后再试!");
        }
    }

    private boolean getTransActivityContentLock(Long headId) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //key 已经存在，则不做任何动作 否则将 key 的值设为 value 设置成功，返回true 否则返回false
        //以headId为key，同一个活动不允许多个计划同时生成文案
        //key的失效时间60秒
        boolean flag = valueOperations.setIfAbsent("activity_trans_lock", String.valueOf(headId),60, TimeUnit.SECONDS);
        return flag;
    }

    /**
     * 删除锁
     */
    private void delTransLock() {
        redisTemplate.delete("activity_trans_lock");
    }

    /**
     * 初始化 执行计划-群组信息
     * @param activityPlan
     * @return
     */
    private void initPlanGroup(ActivityPlan activityPlan)
    {
        //初始化计划选择群组表
        activityPushMapper.deletePlanGroup(activityPlan.getPlanId());

        activityPushMapper.insertActivityPlanGroup(activityPlan.getPlanId());
        //判断计划的类型 如果是通知阶段 则做额外的处理
        if(ActivityPlanTypeEnum.Notify.getPlanTypeCode().equals(activityPlan.getPlanType()))
        {
            //没有活动商品 的活动机制所处的 group为不可选
            activityPushMapper.updateActivityPlanGroup(activityPlan.getPlanId());
            // 成长商品活动属性为否 且没有配文案，则设置 group不可选
            activityPushMapper.updateActivityPlanGroup2(activityPlan.getPlanId(),activityPlan.getStage(),activityPlan.getPlanType());

        }
    }

    /**
     * 实际进行文案转化的类
     * @param planId
     * @return 1表示生成成功 0表示生成失败
     */
    private void transContent(Long headId,Long planId,String activityStage,String activityType) throws Exception
    {
        Long startTime = System.currentTimeMillis();

        //获取此活动上配置的所有模板
        List<Map<String,String>> templateList=activityPushMapper.getAllTemplate(headId,activityStage,activityType);

        Map<String,String> templateMap= Maps.newHashMap();
        for(Map<String,String> template:templateList)
        {
            //以GROUP_ID为key，内容模板作为value
            templateMap.put(String.valueOf(template.get("GROUP_ID")),template.get("TMP_CONTENT"));
        }

        //获取所有活动商品的价格
        List<ActivityProduct> productPriceList=activityProductMapper.getProductPriceList(headId,activityType);
        Map<String,Double> prodPriceMap=productPriceList.stream().collect(Collectors.toMap(ActivityProduct::getProductId,ActivityProduct::getMinPrice,(o1,o2)->o1));

        //根据planId获取当前有多少人需要推送
        int pushUserCount= activityPushMapper.getPushCount(planId);
        int pageSize=400;
        //判断如果条数大于400 则进行分页
        if(pushUserCount<=pageSize)
        {
            List<ActivityDetail> list = activityPushMapper.getPushList(1,pushUserCount,planId);
            //填充模板 生成文案
            List<ActivityContentVO> targetList= null;
            try {
                targetList = processVariable(list,templateMap,prodPriceMap);
            } catch (Exception e) {
                //错误日志上报
                log.error("活动运营转化文案错误，错误堆栈为{}",e);
                //上报
                exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e),1,512));
                //异常向上抛出
                throw e;
            }
            //保存要推送的文案
            if(null!=targetList&&targetList.size()>0)
            {
                activityPushMapper.insertPushContentTemp(targetList);
            }
        }else
        {
            ExecutorService pool = null;
            try {
                ThreadFactory activityThreadFactory = new ThreadFactoryBuilder()
                        .setNameFormat("activity-content-trans-pool-%d").build();

                //生成线程池(8个线程)
                pool = new ThreadPoolExecutor(8,
                        8,
                        1000,
                        TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>(),
                        activityThreadFactory);

                //分页多线程处理
                int page=pushUserCount%pageSize==0?pushUserCount/pageSize:(pushUserCount/pageSize+1);
                List taskList= Lists.newArrayList();
                //生成线程对象列表
                for(int i=0;i<page;i++)
                {
                    taskList.add(new TransActivityContentThread(planId,i*pageSize+1,(i+1)*pageSize,templateMap,prodPriceMap));
                }

                log.info("活动运营转换文案一共需要{}个线程来处理",taskList.size());
                //放入线程池中 等待执行结束

                List<Future<List<ActivityContentVO>>> threadResult=pool.invokeAll(taskList);

                for(Future<List<ActivityContentVO>> future:threadResult)
                {
                    if(null!=future.get()&&future.get().size()>0)
                    {
                        activityPushMapper.insertPushContentTemp(future.get());
                    }else
                    {
                        throw new LinkSteadyException("转化文案失败");
                    }
                }
            } catch (Exception e) {
                //错误日志上报
                log.error("活动运营转化文案错误，错误堆栈为{}",e);
                //上报
                exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e),1,512));
                //异常向上抛出
                throw e;
            }finally {
                pool.shutdown();
            }
        }
        //用临时表更新 活动运营明细表
        activityPushMapper.updatePushContentFromTemp(planId);
        Long endTime = System.currentTimeMillis();
        log.info(">>>活动文案转化成功，共：{}人，耗时：{}", pushUserCount, endTime - startTime);
    }

    /**
     * 对变量进行替换
     * @param list
     * @return
     */
    public List<ActivityContentVO> processVariable(List<ActivityDetail> list, Map<String,String> templateMap,Map<String,Double> prodPriceMap){
        List<ActivityContentVO> targetList = Lists.newArrayList();
        ActivityContentVO activityContentVO = null;
        String smsContent="";

        for(ActivityDetail activityDetail:list)
        {
            //获取文案内容
            smsContent=templateMap.get(activityDetail.getGroupId());

            if(null==smsContent)
            {
                 smsContent="";
            }

            //判断是否含有变量 如果有则进行替换
            smsContent = smsContent.replace("${PROD_NAME}", convertNullToEmpty(activityDetail.getEpbProductName()));

            //判断文案中是否含有价格变量
            if(smsContent.indexOf("${PRICE}")!=-1)
            {
                if(prodPriceMap.containsKey(activityDetail.getEpbProductId()))
                {
                    //替换价格
                    smsContent = smsContent.replace("${PRICE}", BigDecimal.valueOf(prodPriceMap.get(activityDetail.getEpbProductId())).stripTrailingZeros().toEngineeringString());
                }else
                {
                    //替换价格
                    smsContent = smsContent.replace("${PRICE}", convertNullToEmpty(activityDetail.getRecPiecePrice()));
                }
            }


            //判断是否含有商品链接变量
            if(smsContent.indexOf("${PROD_URL}")!=-1)
            {
                //获取商品的短链
                String prodLongUrl=shortUrlService.genProdShortUrlByProdId(activityDetail.getEpbProductId(),"S");
                //如果短链生成错误，则不再进行替换
                if(!"error".equals(prodLongUrl))
                {
                    smsContent = smsContent.replace("${PROD_URL}",prodLongUrl);
                }
            }

            //构造对象
            activityContentVO=new ActivityContentVO();
            activityContentVO.setSmsContent(smsContent);
            activityContentVO.setPlanId(activityDetail.getPlanId());
            activityContentVO.setActivityDetailId(activityDetail.getActivityDetailId());

            targetList.add(activityContentVO);
        }
        return targetList;
    }

    /**
     * 启动推送
     * @param pushMethod
     * @param pushPeriod
     * @param activityPlan
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pushActivity(String pushMethod, String pushPeriod, ActivityPlan activityPlan) throws Exception{
        //根据锁去更新状态 更新为执行中
        int count= activityPushMapper.updateStatus(activityPlan.getPlanId(),ActivityPlanStatusEnum.EXEC.getStatusCode(),activityPlan.getVersion());

        if(count==0)
        {
            throw new LinkSteadyException("记录已被其他用户修改，请在列表界面刷新后重新操作！");
        }else
        {
            //判断推送方式
            String newPushPeriod = "";
            // 立即推送：当前时间往后顺延10分钟
            if ("IMME".equalsIgnoreCase(pushMethod)) {
                newPushPeriod = DateTimeFormatter.ofPattern("yyyyMMddHHmm").format(LocalDateTime.now().plusMinutes(10));
            }else if("FIXED".equalsIgnoreCase(pushMethod)) {

                //当前日期的YYYYMMDD + 用户选择的HHss
                newPushPeriod= DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now())+
                        DateTimeFormatter.ofPattern("HHmm").format(LocalTime.parse(pushPeriod, DateTimeFormatter.ofPattern("HH:mm")));
            }else
            {
                // 默认是AI：存储当前日期YYYYMMDD 在写入detail表的时候拼上用户的个性化推送时间
                newPushPeriod = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
            }
            //将推送方式 和推送时段更新到PLAN表
            activityPushMapper.updatePushMethod(activityPlan.getPlanId(),pushMethod,newPushPeriod);
            //更新detail表
            activityPushMapper.updatePushScheduleDate(activityPlan.getPlanId());

            //此处对活动明细表进行短信文案和推送时间的校验
            //判断文案是否有空
            int validCount=activityDetailMapper.selectContentNulls(activityPlan.getPlanId());
            if(validCount>0)
            {
                throw new LinkSteadyException(validCount+"条文案为空，请核对活动配置！");
            }

            validCount=activityDetailMapper.selectContentLimit(activityPlan.getPlanId(),pushProperties.getSmsLengthLimit());
            //判断文案是否有超字数
            if(validCount>0)
            {
                throw new LinkSteadyException(validCount+"条文案长度超过长度限制，请核对活动配置！");
            }

            //判断文案是否存在非法变量
            validCount=activityDetailMapper.selectContentVariable(activityPlan.getPlanId());
            //判断文案是否有超字数
            if(validCount>0)
            {
                throw new LinkSteadyException(validCount+"条文案存在不符合规范的字符，请核对活动配置！");
            }

            //判断推送时间是否有空
            validCount=activityDetailMapper.selectPushScheduleNulls(activityPlan.getPlanId());

            if(validCount>0)
            {
                throw new LinkSteadyException(validCount+"条推送时间为空，请联系系统运维人员！");
            }

            //判断推送时间格式不正确
            validCount=activityDetailMapper.selectPushScheduleInvalid(activityPlan.getPlanId());
            if(validCount>0)
            {
                throw new LinkSteadyException(validCount+"条推送时间不正确，请联系系统运维人员！");
            }

            activityPushMapper.insertToPushListLarge(activityPlan.getPlanId());

            //更新状态为 执行中(根据当前计划的状态更新不同的状态字段)
            activityHeadService.updateStatus(activityPlan.getHeadId(),
                    activityPlan.getStage(),
                    ActivityStatusEnum.DOING.getStatusCode(),
                    activityPlan.getPlanType());
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePlanToStop(ActivityPlan activityPlan) throws Exception{
        //更新推送计划的状态
        int count= activityPushMapper.updateStatus(activityPlan.getPlanId(),ActivityPlanStatusEnum.STOP.getStatusCode(),activityPlan.getVersion());

        if(count==0)
        {
            throw new LinkSteadyException("活动已被其他用户修改，请返回刷新后重试！");
        }
    }

    /**
     * 判断当前计划的所有活动机制是否都配置了文案
     * @param activityPlan
     * @return
     */
    @Override
    public boolean validateNotifySms(ActivityPlan activityPlan) {
        //仅对通知阶段进行判断
        if(activityPlan.getPlanType().equals(ActivityPlanTypeEnum.Notify.getPlanTypeCode()))
        {
            int count =activityPushMapper.validateNotifySms(activityPlan.getHeadId(),activityPlan.getStage(),activityPlan.getPlanType());

            //如果存在，则校验失败
            return count>0;
        }else
        {
            return false;
        }
    }


    /**
     * 如果传入的字符串为null，则返回空字符串
     * @param obj
     * @return
     */
    private String convertNullToEmpty(String obj)
    {
        if(null==obj)
        {
            return "";
        }else
        {
            return obj;
        }
    }

}
