package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.config.PushConfig;
import com.linksteady.operate.dao.ActivityDetailMapper;
import com.linksteady.operate.dao.ActivityProductMapper;
import com.linksteady.operate.dao.ActivityPushMapper;
import com.linksteady.operate.domain.ActivityDetail;
import com.linksteady.operate.domain.ActivityPlan;
import com.linksteady.operate.domain.enums.ActivityPlanStatusEnum;
import com.linksteady.operate.domain.enums.ActivityStatusEnum;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.ActivityHeadService;
import com.linksteady.operate.service.ActivityPushService;
import com.linksteady.operate.service.ShortUrlService;
import com.linksteady.operate.thread.TransActivityContentThread;
import com.linksteady.operate.vo.ActivityContentVO;
import com.linksteady.smp.starter.lognotice.service.ExceptionNoticeHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

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
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @Autowired
    ActivityProductMapper activityProductMapper;

    @Autowired
    private PushConfig pushConfig;

    /**
     * 对活动推送文案进行转换
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transActivityDetail( ActivityPlan activityPlan) throws Exception {
        //删除临时表中的文案
        activityPushMapper.deleteContentTmp(activityPlan.getPlanId());
        transContent(activityPlan.getHeadId(),activityPlan.getPlanId(),activityPlan.getStage(),activityPlan.getPlanType());
    }

    @Override
    public  boolean getTransActivityContentLock(Long headId) {
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
    @Override
    public void delTransLock() {
        redisTemplate.delete("activity_trans_lock");
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
        log.info("获取到模板的内容为{}",templateList.size());

        Map<String,String> templateMap= Maps.newHashMap();
        for(Map<String,String> template:templateList)
        {
            log.info("获取到的文案内容为{}", JSON.toJSONString(template));
            //以GROUP_ID为key，内容模板作为value
            templateMap.put(String.valueOf(template.get("group_id")),template.get("tmp_content"));
        }

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
                targetList = processVariable(list,templateMap);
            } catch (Exception e) {
                //错误日志上报
                log.error("活动运营转化文案错误，错误堆栈为{}",e);
                //上报
                exceptionNoticeHandler.exceptionNotice(e);
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
                    taskList.add(new TransActivityContentThread(planId,i*pageSize+1,(i+1)*pageSize,templateMap));
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
                exceptionNoticeHandler.exceptionNotice(e);
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
    public List<ActivityContentVO> processVariable(List<ActivityDetail> list, Map<String,String> templateMap){
        List<ActivityContentVO> targetList = Lists.newArrayList();
        DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
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
            smsContent = smsContent.replace("${商品名称}", convertNullToEmpty(activityDetail.getEpbProductName()));

            //判断文案中是否含有价格变量
            smsContent = smsContent.replace("${商品最低单价}",decimalFormat.format(activityDetail.getActivityPrice())+"元");

            //判断是否含有商品链接变量
            if(smsContent.indexOf("${商品详情页短链}")!=-1)
            {
                //获取商品的短链
                String prodLongUrl=shortUrlService.genProdShortUrlByProdId(activityDetail.getEpbProductId(),"S");
                //如果短链生成错误，则不再进行替换
                if(!"error".equals(prodLongUrl))
                {
                    smsContent = smsContent.replace("${商品详情页短链}"," "+prodLongUrl+" ");
                }
            }

            //替换利益点
            smsContent = smsContent.replace("${商品利益点}", getActivityProfilt(activityDetail.getActivityProfit(),activityDetail.getGroupId()));

            //判断是否需要加上签名及退订方式
            //获取签名
            String signature=pushConfig.getSignature();
            //实际推送时是否需要签名
            String signatureFlag=pushConfig.getSendSignatureFlag();

            String unsubscribe=pushConfig.getUnsubscribe();
            //实际推送时是否需要退订信息
            String unsubscribeFlag=pushConfig.getSendUnsubscribeFlag();

            //需要加上签名
            if(null!=signatureFlag&&"Y".equals(signatureFlag))
            {
                smsContent =signature+smsContent;
            }

            //需要加上退订方式
            if(null!=unsubscribeFlag&&"Y".equals(unsubscribeFlag))
            {
                smsContent=smsContent+unsubscribe;
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

    private String getActivityProfilt(double activityProfit,String groupId)
    {
        DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
        if("9".equals(groupId))
        {
            return activityProfit*10+"折";
        }else {
            return decimalFormat.format(activityProfit)+"元";
        }
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

            //计算文案的字数限制 (因为库里存储的长度限制是去掉 签名和 退订信息的)
            int smsLengthLimit=pushConfig.getSmsLengthLimit();
            String signature=pushConfig.getSignature();
            String unsubscribe=pushConfig.getSignature();

             //如果发送时应包含签名信息，则将签名长度加进来
            if("Y".equals(pushConfig.getSendSignatureFlag()))
            {
                smsLengthLimit=smsLengthLimit+(null==signature?0:signature.length());
            }
            if("Y".equals(pushConfig.getSendUnsubscribeFlag()))
            {
                smsLengthLimit=smsLengthLimit+(null==unsubscribe?0:unsubscribe.length());
            }

            validCount=activityDetailMapper.selectContentLimit(activityPlan.getPlanId(),smsLengthLimit);
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
    public boolean validateSmsConfig(ActivityPlan activityPlan) {
        int count =activityPushMapper.validateSmsConfig(activityPlan.getHeadId(),activityPlan.getStage(),activityPlan.getPlanType());
        //如果存在，则校验失败
        return count>0;
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
