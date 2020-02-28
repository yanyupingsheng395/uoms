package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.util.DateUtil;
import com.linksteady.operate.dao.ActivityDetailMapper;
import com.linksteady.operate.dao.ActivityHeadMapper;
import com.linksteady.operate.dao.ActivityPlanMapper;
import com.linksteady.operate.domain.ActivityDetail;
import com.linksteady.operate.domain.ActivityPlan;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.enums.ActivityPlanStatusEnum;
import com.linksteady.operate.domain.enums.ActivityPlanTypeEnum;
import com.linksteady.operate.domain.enums.ActivityStageEnum;
import com.linksteady.operate.domain.enums.ActivityStatusEnum;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.ActivityPlanService;
import com.linksteady.operate.service.ShortUrlService;
import com.linksteady.operate.thread.TransActivityContentThread;
import com.linksteady.operate.vo.ActivityContentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author hxcao
 * @date 2019-11-02
 */
@Service
@Slf4j
public class ActivityPlanServiceImpl implements ActivityPlanService {

    @Autowired
    private ActivityHeadMapper activityHeadMapper;

    @Autowired
    private ActivityPlanMapper activityPlanMapper;

    @Autowired
    private ActivityDetailMapper activityDetailMapper;

    @Autowired
    ShortUrlService shortUrlService;

    @Autowired
    private DailyProperties dailyProperties;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private static final String HAS_PREHEAT="1";

    /**
     * 生成plan数据
     * @param headId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePlanList(String headId, String stage) {
        List<ActivityPlan> planList = Lists.newArrayList();
        Map<String, Date> dateMap = activityHeadMapper.getStageDate(headId);
        //正式开始时间
        Date formalStartDt = dateMap.get("FORMAL_START_DT");
        //正式结束时间
        Date formalEndDt = dateMap.get("FORMAL_END_DT");
        //预热开始时间
        Date preheatStartDt = dateMap.get("PREHEAT_START_DT");
        //预热结束时间
        Date preheatEndDt = dateMap.get("PREHEAT_END_DT");

        //预热提醒时间
        Date preheatNotifyDt = dateMap.get("PREHEAT_NOTIFY_DT");
        //正式提醒时间
        Date formalNotifyDt = dateMap.get("FORMAL_NOTIFY_DT");

        if(stage.equals(ActivityStageEnum.formal.getStageCode())) {
            //写入正式的提醒记录
            planList.add(new ActivityPlan(Long.valueOf(headId),
                    formalNotifyDt,
                    ActivityStageEnum.formal.getStageCode(),
                    ActivityPlanTypeEnum.Notify.getPlanTypeCode()));

            LocalDate formalStart = DateUtil.dateToLocalDate(formalStartDt);
            LocalDate formalEnd = DateUtil.dateToLocalDate(formalEndDt);
            //写入正式的记录
            while(formalStart.isBefore(formalEnd)) {
                planList.add(new ActivityPlan(Long.valueOf(headId),
                        DateUtil.localDateToDate(formalStart),
                        ActivityStageEnum.formal.getStageCode(),
                        ActivityPlanTypeEnum.During.getPlanTypeCode()));

                formalStart = formalStart.plusDays(1);
            }
            planList.add(new ActivityPlan(Long.valueOf(headId),
                    DateUtil.localDateToDate(formalEnd),
                    ActivityStageEnum.formal.getStageCode(),
                    ActivityPlanTypeEnum.During.getPlanTypeCode()));
        }

        // 包含预售
        if(stage.equalsIgnoreCase(ActivityStageEnum.preheat.getStageCode())) {
            //写入预售的提醒记录
            planList.add(new ActivityPlan(Long.valueOf(headId),
                    preheatNotifyDt,
                    ActivityStageEnum.preheat.getStageCode(),
                    ActivityPlanTypeEnum.Notify.getPlanTypeCode()));

            LocalDate start = DateUtil.dateToLocalDate(preheatStartDt);
            LocalDate end = DateUtil.dateToLocalDate(preheatEndDt);
            //写入预售的记录
            while(start.isBefore(end)) {
                planList.add(new ActivityPlan(Long.valueOf(headId),
                        DateUtil.localDateToDate(start),
                        ActivityStageEnum.preheat.getStageCode(),
                        ActivityPlanTypeEnum.During.getPlanTypeCode()));
                start = start.plusDays(1);
            }
            planList.add(new ActivityPlan(Long.valueOf(headId),
                    DateUtil.localDateToDate(end),
                    ActivityStageEnum.preheat.getStageCode(),
                    ActivityPlanTypeEnum.During.getPlanTypeCode()));
        }
        activityPlanMapper.savePlanList(planList);
    }

    @Override
    public List<ActivityPlan> getPlanList(String headId) {
        return activityPlanMapper.getPlanList(headId);
    }

    @Override
    public int updateStatus(String headId, String planDateWid, String status,int version) {
        return  activityPlanMapper.updateStatus(headId, planDateWid, status,version);
    }

    @Override
    public void deleteData(String headId) {
        activityPlanMapper.deleteData(headId);
    }

    @Override
    public void insertToPushListLarge(String headId, String planDateWid) {
        activityPlanMapper.insertToPushListLarge(headId,planDateWid);
    }

    @Override
    public int getStatusCount(String headId, String stage, List<String> asList) {
        return activityPlanMapper.getStatusCount(headId, stage, asList);
    }

    @Override
    public List<Map<String,Object>> getUserGroupList(String headId, String planDtWid) {
        return activityPlanMapper.getUserGroupList(headId, planDtWid);
    }

    @Override
    public ActivityPlan getPlanInfo(String headId, String planDtWid) {
        return activityPlanMapper.getPlanInfo(headId,planDtWid);
    }

    /**
     * 对活动推送文案进行转换 0 失败 1 成功 2其它用户操作
     * @param headId
     * @param planDtWid
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String transActivityDetail(String headId, String planDtWid) {
        //获取锁
        if(getTransActivityContentLock(headId,planDtWid))
        {
            //删除临时表中的文案
            activityDetailMapper.deleteContentTmp(headId);
             String result=transContent(headId,planDtWid);
             //释放锁
            delTransLock();
            //返回结果
            return result;
        }else
        {
            //其它用户正在操作
            return "2";
        }
    }

    private boolean getTransActivityContentLock(String headId,String planDtWid) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //key 已经存在，则不做任何动作 否则将 key 的值设为 value 设置成功，返回true 否则返回false
        //以headId为key，同一个活动不允许多个计划同时生成文案
        //key的失效时间60秒
        boolean flag = valueOperations.setIfAbsent("activity_trans_lock", headId,60, TimeUnit.SECONDS);
        return flag;
    }

    private void delTransLock() {
        redisTemplate.delete("activity_trans_lock");
    }

    /**
     * 实际进行文案转化的类
     * @param headId
     * @param planDtWid
     * @return 1表示生成成功 0表示生成失败
     */
    private String transContent(String headId, String planDtWid)
    {
        String result="1";
        Long startTime = System.currentTimeMillis();
        //获取此活动上配置的所有模板
        List<Map<String,String>> templateList=activityPlanMapper.getAllTemplate(headId);

        Map<String,String> templateMap= Maps.newHashMap();
        for(Map<String,String> template:templateList)
        {
            //以GROUP_ID+"_"+ACTIVITY_STAGE+"_"+ACTIVITY_TYPE为key，内容模板作为value
            templateMap.put(template.get("GROUPNAME"),template.get("TMP_CONTENT"));
        }

        //根据headerID获取当前有多少人需要推送
        int pushUserCount= activityDetailMapper.getDataCount(headId,Long.parseLong(planDtWid),"-1");
        int pageSize=200;
        //判断如果条数大于200 则进行分页
        if(pushUserCount<=pageSize)
        {
            List<ActivityDetail> list = activityDetailMapper.getPageList(1,pushUserCount,headId,Long.parseLong(planDtWid),"-1");
            //填充模板 生成文案
            List<ActivityContentVO> targetList= null;
            try {
                targetList = processVariable(list,templateMap);
            } catch (Exception e) {
               result="0";
            }
            //保存要推送的文案
            if(null!=targetList&&targetList.size()>0)
            {
                activityDetailMapper.insertPushContentTemp(targetList);
            }
        }else
        {
            ExecutorService pool = null;
            try {
                //生成线程池 (线程数量为4)
                pool = new ThreadPoolExecutor(8, 8, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());;

                //分页多线程处理
                int page=pushUserCount%pageSize==0?pushUserCount/pageSize:(pushUserCount/pageSize+1);
                List taskList= Lists.newArrayList();
                //生成线程对象列表
                for(int i=0;i<page;i++)
                {
                    taskList.add(new TransActivityContentThread(planDtWid,headId,i*pageSize+1,(i+1)*pageSize,templateMap));
                }

                log.info("活动运营转换文案一共需要{}个线程来处理",taskList.size());
                //放入线程池中 等待执行结束

                List<Future<List<ActivityContentVO>>> threadResult=pool.invokeAll(taskList);

                for(Future<List<ActivityContentVO>> future:threadResult)
                {
                    if(null!=future.get()&&future.get().size()>0)
                    {
                        activityDetailMapper.insertPushContentTemp(future.get());
                    }else
                    {
                        //存在错误
                        result="0";
                        break;
                    }
                }

            } catch (Exception e) {
                //错误日志上报
                log.error("活动运营转化文案错误，错误堆栈为{}",e);
                result="0";
            }finally {
                pool.shutdown();
            }
        }

        if(result=="1")
        {
            //用临时表更新 活动运营明细表
            activityDetailMapper.updatePushContentFromTemp(headId);
            Long endTime = System.currentTimeMillis();
            log.info(">>>活动文案转化成功，共：{}人，耗时：{}", pushUserCount, endTime - startTime);
        }else
        {
            Long endTime = System.currentTimeMillis();
            log.info(">>>活动文案转化失败，共：{}人，耗时：{}", pushUserCount, endTime - startTime);
        }
        return result;
    }

    /**
     * 对变量进行替换
     * @param list
     * @return
     */
    @Override
    public List<ActivityContentVO> processVariable(List<ActivityDetail> list, Map<String,String> templateMap){
        List<ActivityContentVO> targetList = Lists.newArrayList();
        ActivityContentVO activityContentVO = null;
        String smsContent="";

        for(ActivityDetail activityDetail:list)
        {
            //获取文案内容
            smsContent=templateMap.get(activityDetail.getGroupId()+"_"+activityDetail.getActivityStage()+"_"+activityDetail.getPlanType());

            if(null==smsContent)
            {
                 smsContent="";
            }

            //判断是否含有变量 如果有则进行替换
            smsContent = smsContent.replace("${PROD_NAME}", convertNullToEmpty(activityDetail.getEpbProductName()));
            //替换价格
            smsContent = smsContent.replace("${PRICE}", convertNullToEmpty(activityDetail.getRecPiecePrice()));

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
            activityContentVO.setHeadId(activityDetail.getHeadId());
            activityContentVO.setActivityDetailId(activityDetail.getActivityDetailId());

            targetList.add(activityContentVO);
        }
        return targetList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pushActivity(String headId, String planDateWid, String pushMethod, String pushPeriod, ActivityPlan activityPlan) throws Exception{
        //根据锁去更新状态 更新为执行中
        int count=updateStatus(headId, planDateWid, ActivityPlanStatusEnum.EXEC.getStatusCode(),activityPlan.getVersion());

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
            activityPlanMapper.updatePushMethod(headId,planDateWid,pushMethod,newPushPeriod);
            //更新detail表
            activityDetailMapper.updatePushScheduleDate(headId,planDateWid);

            //此处对活动明细表进行短信文案和推送时间的校验
            String validateResult=validateActivityDetail(headId,planDateWid);
            if("SUCCESS".equals(validateResult))
            {
                insertToPushListLarge(headId, planDateWid);

                //预售
                if(ActivityStageEnum.preheat.getStageCode().equals(activityPlan.getStage()))
                {
                    //更新预售的状态为 执行中
                    activityHeadMapper.updatePreheatStatusHead(headId, ActivityStatusEnum.DOING.getStatusCode());
                }else
                {
                    //更新正式状态为 执行中
                    activityHeadMapper.updateFormalStatusHead(headId,ActivityStatusEnum.DOING.getStatusCode());
                }
            }else
            {
                //如果校验失败  抛出异常 数据进行回滚
                throw new LinkSteadyException(validateResult);
            }
        }
    }

    private String validateActivityDetail(String headId,String planDateWid)
    {
        //判断文案是否有空
        int count=activityDetailMapper.selectContentNulls(headId,planDateWid);
        if(count>0)
        {
            return count+"条文案为空，请核对活动配置！";
        }

        count=activityDetailMapper.selectContentLimit(headId,planDateWid,dailyProperties.getSmsLengthLimit());
        //判断文案是否有超字数
        if(count>0)
        {
            return count+"条文案长度超过长度限制，请核对活动配置！";
        }

        //判断文案是否存在非法变量
        count=activityDetailMapper.selectContentVariable(headId,planDateWid);
        //判断文案是否有超字数
        if(count>0)
        {
            return count+"条文案存在不符合规范的字符，请核对活动配置！";
        }

        //判断推送时间是否有空
        count=activityDetailMapper.selectPushScheduleNulls(headId,planDateWid);

        if(count>0)
        {
            return count+"条推送时间不正确，请联系系统运维人员！";
        }

        //判断推送时间格式不正确
        count=activityDetailMapper.selectPushScheduleInvalid(headId,planDateWid);
        if(count>0)
        {
            return count+"条推送时间不正确，请联系系统运维人员！";
        }

        return "SUCCESS";

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
