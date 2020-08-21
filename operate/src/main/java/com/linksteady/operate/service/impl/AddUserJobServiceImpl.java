package com.linksteady.operate.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.linksteady.common.util.ArithUtil;
import com.linksteady.common.util.SpringUtils;
import com.linksteady.operate.dao.AddUserMapper;
import com.linksteady.operate.dao.AddUserTriggerMapper;
import com.linksteady.operate.dao.QywxParamMapper;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.service.AddUserJobService;
import com.linksteady.operate.vo.QywxScheduleEffectVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author huang
 * @date 2020/8/14
 */
@Service
@Slf4j
public class AddUserJobServiceImpl implements AddUserJobService {

    @Autowired
    AddUserMapper addUserMapper;

    @Autowired
    AddUserTriggerMapper addUserTriggerMapper;

    @Autowired
    QywxParamMapper qywxParamMapper;

    @Override
    public void updateTriggerStatus() {
        //更新最近三天的推送结果
        addUserTriggerMapper.updatePushResult();
        //对于执行中的计划，如果所有的用户都推送完了，更新其状态为完成
        addUserTriggerMapper.updateScheduleToDone();
        //更新头表状态为停止
        addUserTriggerMapper.updateHeadToStop();
        //todo更新企业微信拉新状态
        addUserTriggerMapper.updateQywxAddInfo();
    }

    @Override
    public void updateTriggerEffect() {
        //1.更新schedule表上的效果字段
        addUserTriggerMapper.calculateScheduleEffect();
        //2.更新头表 (成功发送申请数量 通过申请数量 申请通过率)
        addUserTriggerMapper.calculateHeadEffect();
        //3.写逐日的转化统计表
        //3.1 删除最近10天的schedule统计数
        addUserTriggerMapper.deleteScheduleEffect();
        //3.2 写入最近10天内schedule的逐日转化统计数
        //获取最近10天的schedule列表
        List<AddUserSchedule> scheduleList=addUserTriggerMapper.getLastScheduleList();

        List<QywxScheduleEffectVO> covList=null;
        Map<String,Long> covMap=null;
        LocalDate statisDate=null;
        List<AddUserEffect> addUserEffectList=null;
        AddUserEffect addUserEffect=null;

        for(AddUserSchedule addUserSchedule:scheduleList)
        {
            addUserEffectList= Lists.newArrayList();
            LocalDate scheduleDate=addUserSchedule.getScheduleDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            //获取这个schdule的所有转化统计数据
            covList=addUserTriggerMapper.getScheduleCovStatis(addUserSchedule.getScheduleId());
            covMap=covList.stream().collect(Collectors.toMap(QywxScheduleEffectVO::getStatisDateWid, QywxScheduleEffectVO::getCnt));

            for(int j=0;j<10;j++)
            {
                statisDate=scheduleDate.plusDays(j);
                if(statisDate.isAfter(LocalDate.now()))
                {
                    break;
                }

                addUserEffect=new AddUserEffect();
                addUserEffect.setHeadId(addUserSchedule.getHeadId());
                addUserEffect.setScheduleId(addUserSchedule.getScheduleId());
                addUserEffect.setApplyDate(addUserSchedule.getScheduleDate());
                addUserEffect.setApplyNum(addUserSchedule.getApplySuccessNum());

                addUserEffect.setStatisDate(statisDate);
                addUserEffect.setStatisDay(j);

                String key=statisDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                //判断这个日期是否有转化数据
                if(covMap.containsKey(key))
                {
                    addUserEffect.setStatisPassNum(covMap.get(key));
                    //转化率
                    addUserEffect.setStatisPassRate(addUserSchedule.getApplySuccessNum()==0?0d: ArithUtil.formatDouble(covMap.get(key)/addUserSchedule.getApplySuccessNum()*100.00,2));
                }else
                {
                    addUserEffect.setStatisPassNum(0);
                    addUserEffect.setStatisPassRate(0d);
                }
                addUserEffectList.add(addUserEffect);
            }
            addUserTriggerMapper.saveScheduleEffect(addUserEffectList);
        }
    }

    @Override
    public void updateAddUserStatus() {
        //更新最近三天的推送结果
        addUserMapper.updatePushResult();
        //对于 执行中的计划 判断其下面所有的用户都完成了推送，如果是，则更新其状态为 完成
        addUserMapper.updateScheduleToDone();
        //更新头表的状态(存在执行中的计划，则为执行中 否则为停止  如果所有的人都推送完了，则为已结束 )
        addUserMapper.updateHeadToStop();
        addUserMapper.updateHeadToDone();
        //todo 更新企业微信拉新状态
    }

    @Override
    public void updateAddUserEffect() {
        //1.更新schedule表上的效果字段
        addUserMapper.calculateScheduleEffect();
        //2.更新头表 (成功发送申请数量 通过申请数量 申请通过率)
        addUserMapper.calculateHeadEffect();
        //3.写逐日的转化统计表
        //3.1 删除最近10天的schedule统计数
        addUserMapper.deleteScheduleEffect();
        //3.2 写入最近10天内schedule的逐日转化统计数
        //获取最近10天的schedule列表
        List<AddUserSchedule> scheduleList=addUserMapper.getLastScheduleList();

        List<QywxScheduleEffectVO> covList=null;
        Map<String,Long> covMap=null;
        List<AddUserEffect> addUserEffectList=null;
        AddUserEffect addUserEffect=null;
        LocalDate statisDate=null;

        for(AddUserSchedule addUserSchedule:scheduleList)
        {
            addUserEffectList= Lists.newArrayList();
            LocalDate scheduleDate=addUserSchedule.getScheduleDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            //获取这个schdule的所有转化统计数据
            covList=addUserMapper.getScheduleCovStatis(addUserSchedule.getScheduleId());
            // key为日期 value为转化人数
            covMap=covList.stream().collect(Collectors.toMap(QywxScheduleEffectVO::getStatisDateWid, QywxScheduleEffectVO::getCnt));

            //固定往后面算10天
            for(int j=0;j<10;j++)
            {
                statisDate=scheduleDate.plusDays(j);
                if(statisDate.isAfter(LocalDate.now()))
                {
                    break;
                }

                addUserEffect=new AddUserEffect();
                addUserEffect.setHeadId(addUserSchedule.getHeadId());
                addUserEffect.setScheduleId(addUserSchedule.getScheduleId());
                addUserEffect.setApplyDate(addUserSchedule.getScheduleDate());
                addUserEffect.setApplyNum(addUserSchedule.getApplySuccessNum());

                addUserEffect.setStatisDate(statisDate);
                addUserEffect.setStatisDay(j);

                String key=statisDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                //判断这个日期是否有转化数据
                if(covMap.containsKey(key))
                {
                    addUserEffect.setStatisPassNum(covMap.get(key));
                    //转化率
                    addUserEffect.setStatisPassRate(addUserSchedule.getApplySuccessNum()==0?0d: ArithUtil.formatDouble(covMap.get(key)/addUserSchedule.getApplySuccessNum()*100.00,2));
                }else
                {
                    addUserEffect.setStatisPassNum(0);
                    addUserEffect.setStatisPassRate(0d);
                }
                addUserEffectList.add(addUserEffect);
            }
            addUserMapper.saveScheduleEffect(addUserEffectList);
        }

    }

    /**
     * 自动处理每日生成的订单，并生成文案，进行短信推送
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processDailyOrders() throws Exception{
       //首先看当前是否有处于running的schedule
        int runingCount=addUserTriggerMapper.getRunningScheduleCount();
       if(runingCount==1)
       {
           //获取当前计划上还有多少容量 如果容量为0，则给出提示；否则从排队表中取出对应的推送数据，放入到短信推送表里去；
           AddUserSchedule addUserSchedule=addUserTriggerMapper.getRunningSchedule();

           if(addUserSchedule==null||addUserSchedule.getRemainUserCnt()==0)
           {
               log.error("企业微信-主动拉新:无法获取到运行中的推送计划或推送计划无剩余数量。");
               return;
           }

           //获取当前的时间戳
           QywxParam qywxParam=qywxParamMapper.getQywxParam();

           //如果时间戳距现在已经过去?3天，则不再使用这个时间戳，使用新的时间戳，sysdate-1
           long lastTimes= Timestamp.valueOf(qywxParam.getLastSyncOrderDt()).getTime();
           long now= Timestamp.valueOf(LocalDateTime.now()).getTime();

           LocalDateTime orderDt=null;
           //计算两个时间的时间差 (如果超过4天，则舍弃原来的时间，否则还用原来的)
           long diff=now-lastTimes;
           if(diff<=345600000l)
           {
              orderDt=qywxParam.getLastSyncOrderDt();
           }else
           {
               //超过2天，则用当前时间往前推2天
               orderDt=LocalDateTime.now().minusDays(4);
           }

           if(orderDt==null)
           {
               orderDt=LocalDateTime.now().minusDays(4);
           }
           log.info("企业微信-主动拉新：本次处理的订单时间戳为{}",orderDt);
           LocalDateTime currentTimes= LocalDateTime.now();
           //根据时间戳去获取订单、渠道、商品，生成待推送名单，放入排队表 (同一个人，如果重复，则只取第一个)
           List<AddUserTriggerQueue> orderUserList=addUserTriggerMapper.getOrders(orderDt);

           if(null!=orderUserList||orderUserList.size()>=0)
           {
               List<String> sourceIds= Splitter.on(',').trimResults().omitEmptyStrings().splitToList(addUserSchedule.getSourceId());
               List<String> regionIds= Splitter.on(',').trimResults().omitEmptyStrings().splitToList(addUserSchedule.getRegionId());

               boolean sourceFlag=true;
               boolean regionFlag=true;
               List<AddUserTriggerQueue> afterFilterList=Lists.newArrayList();
               for(AddUserTriggerQueue addUserTriggerQueue:orderUserList)
               {
                   //如果选择了渠道条件，则进行渠道条件的判断
                   if(null!=sourceIds&&sourceIds.size()>0)
                   {
                       //任务选择的渠道不包含当前订单的渠道
                       if(!sourceIds.contains(addUserTriggerQueue.getSourceId()))
                       {
                           sourceFlag=false;
                       }
                   }

                   if(null!=regionIds&&regionIds.size()>0)
                   {
                       //当前订单上用户的地域信息 100000,100010 这种格式，省代码,市代码
                       List<String> currRegions=Splitter.on(',').trimResults().omitEmptyStrings().splitToList(addUserTriggerQueue.getRegionId());

                       //如果当前用户没有地域信息 直接排除掉
                       if(null==currRegions||currRegions.size()==0)
                       {
                           regionFlag=false;
                       }

                       //如果用户的 省、市 任何一个代码在当前任务的条件内，则返回true，且不再进行后续循环
                       for(String uRegionId:currRegions)
                       {
                           if(regionIds.contains(uRegionId))
                           {
                               regionFlag=true;
                               break;
                           }
                       }
                   }

                   if(sourceFlag&&regionFlag)
                   {
                       afterFilterList.add(addUserTriggerQueue);
                   }
               }

               //写入排队队列 (如果手机号已经在排队表中，则忽略)
               if(afterFilterList.size()>0)
               {
                   addUserTriggerMapper.addToTriggerQueue(afterFilterList);
               }else
               {
                   log.info("企业微信-主动拉新：订单筛选之后数量为0，无任何需要推送。");
               }
           }

           //剩余槽位数
           long remainCnt=addUserSchedule.getRemainUserCnt();

           //获取排队表本次要处理的最大ID、实际数量
           Map<String,Long> triggerQueueInfo=addUserTriggerMapper.getTriggerQueueInfo(remainCnt);

           long recordNum=triggerQueueInfo.get("record_num");
           if(recordNum>0)
           {
               long queueId=triggerQueueInfo.get("max_queue_id");

               //处理排队表中的数据
               processQueueDataToUserList(recordNum,queueId,addUserSchedule);
           }else
           {
               log.info("企业微信-主动拉新:本批次排队表中无要处理的数据!");
           }

           //更新参数表中的时间戳字段
           LocalDateTime nextSyncDt=currentTimes.minusMinutes(5);
           qywxParamMapper.updateOrderSyncTimes(nextSyncDt);
           log.info("企业微信-主动拉新：订单处理完成");
       }else
       {
           log.error("企业微信-主动拉新:当前无处于running状态的计划或有多个计划！");
       }
    }

    /**
     * 处理排队表中的数据
     */
    public void processQueueDataToUserList(long recordNum,long queueId,AddUserSchedule addUserSchedule) throws Exception
    {
        int pageSize=100;
        long pageNum=recordNum%pageSize==0?recordNum/pageSize:(recordNum/pageSize+1);

        List<AddUserTriggerQueue> queueData=null;
        String smsContent="";
        AddUser addUser=null;
        List<AddUser> adduserList=null;
        long actualApplyNum=0;

        for(int m=0;m<pageNum;m++)
        {
            //获取排队表的数据
            queueData=addUserTriggerMapper.getTriggerQueueData(queueId,pageSize,m*pageSize);
            adduserList=Lists.newArrayList();

            for(AddUserTriggerQueue addUserTriggerQueue:queueData)
            {
                //此处需要判断此用户在N天之内是否推送过拉新短信
                try {
                    SpringUtils.getAopProxy(this).insertToHistory(addUserTriggerQueue.getMobile());
                } catch (Exception e) {
                    log.debug("{}写入历史表错误，原因:{}",addUserTriggerQueue.getMobile(),e);
                    continue;
                }

                //获取文案
                smsContent=addUserSchedule.getSmsContent();
                if(null==smsContent)
                {
                    smsContent="";
                }
                smsContent=smsContent.replace("${渠道名称}",addUserTriggerQueue.getSourceName());
                smsContent=smsContent.replace("${商品名称}",addUserTriggerQueue.getProductName());

                if(StringUtils.isEmpty(smsContent)||StringUtils.isEmpty(addUserTriggerQueue.getSourceName())||StringUtils.isEmpty(addUserTriggerQueue.getProductName()))
                {
                    log.error("企业微信-主动拉新:短信内容或渠道名称或商品名称有空的情况发生!");
                    throw new Exception("企业微信-主动拉新:短信内容或渠道名称或商品名称有空的情况发生!");
                }


                addUser=new AddUser();
                addUser.setHeadId(addUserSchedule.getHeadId());
                addUser.setUserId(addUserTriggerQueue.getUserId());
                addUser.setPhoneNum(addUserTriggerQueue.getMobile());
                addUser.setIsPush("0");
                addUser.setPushStatus("X");
                addUser.setInsertDt(new Date());
                addUser.setUpdateDt(new Date());
                addUser.setSmsContent(smsContent);
                addUser.setState(addUserSchedule.getState());
                addUser.setScheduleId(addUserSchedule.getScheduleId());
                adduserList.add(addUser);
            }

            actualApplyNum+=adduserList.size();
            //写入推送明细
            if(adduserList.size()>0)
            {
                addUserTriggerMapper.insertAddUserList(adduserList);
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
                long scheduleDate= Long.parseLong(LocalDateTime.now().plusHours(1).format(dateTimeFormatter));
                //将推送表放入短信通道表
                addUserTriggerMapper.pushToPushListLarge(adduserList,scheduleDate);
            }

        }

        //删除排队表中本次处理完的数据
        addUserTriggerMapper.deleteTriggerQueue(queueId);

        //更新schedule表中的剩余数量
        addUserTriggerMapper.updateScheduleRemainUserCnt(addUserSchedule.getScheduleId(),recordNum,actualApplyNum);

        //更新头表中的推送数量
        addUserTriggerMapper.updateHeadApplyUserCnt(addUserSchedule.getHeadId(),actualApplyNum);

    }

    /**
     * 删除推送历史表中超过7日的数据
     */
    @Override
    public void deleteAddUserHistory()
    {
        qywxParamMapper.deleteAddUserHistory(7);
    }

    /**
     * 将手机号放入推送历史表中 使用新的事务
     * @param phoneNum
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    public void insertToHistory(String phoneNum) throws Exception
    {
        qywxParamMapper.insertAddUserHistory(phoneNum);
    }
}
