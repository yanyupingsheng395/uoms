package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.linksteady.common.domain.QywxMessage;
import com.linksteady.common.domain.enums.ConfigEnum;
import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.config.PushConfig;
import com.linksteady.operate.dao.QywxActivityDetailMapper;
import com.linksteady.operate.dao.QywxActivityProductMapper;
import com.linksteady.operate.dao.QywxActivityPushMapper;
import com.linksteady.operate.dao.QywxDailyDetailMapper;
import com.linksteady.operate.domain.QywxActivityDetail;
import com.linksteady.operate.domain.QywxActivityPlan;
import com.linksteady.operate.domain.QywxDailyDetail;
import com.linksteady.operate.domain.QywxPushList;
import com.linksteady.operate.domain.enums.ActivityPlanStatusEnum;
import com.linksteady.operate.domain.enums.ActivityStatusEnum;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.*;
import com.linksteady.operate.thread.TransActivityContentThread;
import com.linksteady.operate.thread.TransQywxActivityContentThread;
import com.linksteady.operate.vo.FollowUserVO;
import com.linksteady.operate.vo.QywxActivityContentTmp;
import com.linksteady.smp.starter.lognotice.service.ExceptionNoticeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
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
public class QywxActivityPushServiceImpl implements QywxActivityPushService {

    @Autowired
    private QywxActivityHeadService qywxActivityHeadService;

    @Autowired(required = false)
    private QywxActivityDetailMapper qywxActivityDetailMapper;

    @Autowired(required = false)
    private QywxActivityPushMapper qywxActivityPushMapper;

    @Autowired
    ConfigService configService;

    @Autowired
    ShortUrlService shortUrlService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @Autowired(required = false)
    QywxActivityProductMapper activityProductMapper;

    @Autowired
    private QywxMessageService qywxMessageService;

    @Autowired
    QywxMdiaService qywxMdiaService;

    /**
     * 对活动推送文案进行转换
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transActivityDetail( QywxActivityPlan activityPlan) throws Exception {
        //删除临时表中的文案
        qywxActivityPushMapper.deleteContentTmp(activityPlan.getPlanId());
        transContent(activityPlan.getHeadId(),activityPlan.getPlanId(),activityPlan.getPlanType());
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
    private void transContent(Long headId,Long planId,String activityType) throws Exception
    {
        Long startTime = System.currentTimeMillis();
        //获取此活动上配置的所有模板
        List<Map<String,String>> templateList=qywxActivityPushMapper.getAllTemplate(headId,activityType);
        log.info("获取到模板的内容为{}",templateList.size());

        Map<String,String> templateMap= Maps.newHashMap();
        for(Map<String,String> template:templateList)
        {
            log.info("获取到的文案内容为{}", JSON.toJSONString(template));
            //以GROUP_ID为key，内容模板作为value
            templateMap.put(String.valueOf(template.get("group_id")),template.get("tmp_content"));
        }

        //本次所有商品的mediaId
        Map<String,String> mediaMap=Maps.newHashMap();
        List<String> productIdList=qywxActivityPushMapper.getProductIdList(planId);
        for(int i=0;i<productIdList.size();i++)
        {
            mediaMap.put(productIdList.get(i),qywxMdiaService.getMpMediaId(productIdList.get(i)));
        }

        //根据planId获取当前有多少人需要推送
        int pushUserCount= qywxActivityPushMapper.getPushCount(planId);
        int pageSize=400;
        //判断如果条数大于400 则进行分页
        if(pushUserCount<=pageSize){
            List<QywxActivityDetail> list = qywxActivityPushMapper.getPushList(pushUserCount,0,planId);
            //填充模板 生成文案
            List<QywxActivityContentTmp> targetList= null;
            try {
                targetList = processVariable(list,templateMap,mediaMap);
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
                qywxActivityPushMapper.insertPushContentTemp(targetList);
            }
        }else{
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
                    taskList.add(new TransQywxActivityContentThread(planId,pageSize,i*pageSize,templateMap,mediaMap));
                }

                log.info("活动运营转换文案一共需要{}个线程来处理",taskList.size());
                //放入线程池中 等待执行结束

                List<Future<List<QywxActivityContentTmp>>> threadResult=pool.invokeAll(taskList);

                for(Future<List<QywxActivityContentTmp>> future:threadResult)
                {
                    if(null!=future.get()&&future.get().size()>0)
                    {
                        qywxActivityPushMapper.insertPushContentTemp(future.get());
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
        qywxActivityPushMapper.updatePushContentFromTemp(planId);
        Long endTime = System.currentTimeMillis();
        log.info(">>>活动文案转化成功，共：{}人，耗时：{}", pushUserCount, endTime - startTime);
    }

    /**
     * 对变量进行替换
     * @param list
     * @return
     */
    public List<QywxActivityContentTmp> processVariable(List<QywxActivityDetail> list, Map<String,String> templateMap,Map<String,String> mediaMap){
        List<QywxActivityContentTmp> targetList = Lists.newArrayList();
        DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
        QywxActivityContentTmp activityContentVO = null;
        String smsContent="";

        for(QywxActivityDetail activityDetail:list)
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

            //替换利益点
            smsContent = smsContent.replace("${商品利益点}", getActivityProfilt(activityDetail.getActivityProfit(),activityDetail.getGroupId()));

            //构造对象
            activityContentVO=new QywxActivityContentTmp();
            activityContentVO.setTextContent(smsContent);
            activityContentVO.setPlanId(activityDetail.getPlanId());
            activityContentVO.setQywxDetailId(activityDetail.getQywxDetailId());
            activityContentVO.setMpTitle(activityDetail.getMpTitle());
            activityContentVO.setMpUrl(activityDetail.getMpUrl());
            activityContentVO.setMpMediaId(mediaMap.get(activityDetail.getEpbProductId()));
            activityContentVO.setQywxMsgSign(new Md5Hash(smsContent+"|"+activityDetail.getMpTitle()+"|"+activityDetail.getMpUrl()).toString());

            targetList.add(activityContentVO);
        }
        return targetList;
    }

    private String getActivityProfilt(double activityProfit,String groupId)
    {
        DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
        if("9".equals(groupId))
        {
            return decimalFormat.format(activityProfit)+"折";
        }else {
            return decimalFormat.format(activityProfit)+"元";
        }
    }

    /**
     * 启动推送
     * @param activityPlan
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void pushActivity(QywxActivityPlan activityPlan) throws Exception{
        //根据锁去更新状态 更新为执行中
        int count= qywxActivityPushMapper.updateStatus(activityPlan.getPlanId(),ActivityPlanStatusEnum.EXEC.getStatusCode(),activityPlan.getVersion());
        if(count==0)
        {
            throw new LinkSteadyException("记录已被其他用户修改，请在列表界面刷新后重新操作！");
        }else
        {
            //判断文案是否都进行了配置
            int validCount=qywxActivityDetailMapper.selectContentNulls(activityPlan.getPlanId());
            if(validCount>0)
            {
                throw new LinkSteadyException(validCount+"条文案为空，请核对活动配置！");
            }
            Long headId = activityPlan.getHeadId();
            String appId = qywxMessageService.getMpAppId();
            //按导购分组
            List<FollowUserVO> followUserIdList =qywxActivityPushMapper.getFollowUserList(activityPlan.getPlanId());
            followUserIdList.forEach(x -> {
                String followUserId = x.getFollowUserId();
                // 推送消息(按消息分组)
                List<String> msgSignList =qywxActivityPushMapper.getMessageSignList(headId,followUserId);
                //备注：同一msgSignList下必然是同一个商品
                msgSignList.forEach(y -> {
                    //查询当前签名、当前活动的总记录条数
                    int waitCount=qywxActivityPushMapper.getWaitQywxUserListCount(headId,followUserId,y);
                    int pageSize = 10000;
                    //微信推送一次只能推送最多一万条
                    if(waitCount <= pageSize){
                        log.info("当前推送数据量<=10000");
                        if(waitCount>0){
                            //获取当前待推送的列表
                            List<QywxActivityDetail> qywxActivityDetailList=qywxActivityPushMapper.getQywxUserList(headId,followUserId,y,waitCount,0);

                            //组织推送信息
                            QywxPushList qywxPushList=new QywxPushList();
                            qywxPushList.setTextContent(qywxActivityDetailList.get(0).getSmsContent());
                            qywxPushList.setMpTitle(qywxActivityDetailList.get(0).getMpTitle());
                            qywxPushList.setMpUrl(qywxActivityDetailList.get(0).getMpUrl());
                            qywxPushList.setMpMediaId(qywxActivityDetailList.get(0).getMpMediaId());
                            qywxPushList.setMpAppid(appId);
                            //获取推送外部联系人列表。放入pushlist表中。
                            qywxPushList.setExternalContactIds(org.apache.commons.lang3.StringUtils.join(qywxActivityDetailList.stream().map(QywxActivityDetail::getQywxContractId).collect(Collectors.toList()),","));
                            qywxPushList.setFollowUserId(followUserId);
                            qywxPushList.setSourceId(qywxActivityDetailList.get(0).getQywxDetailId());
                            qywxActivityPushMapper.insertPushList(qywxPushList);
                            //推送并更新状态
                            pushQywxMsg(qywxPushList,qywxActivityDetailList);
                        }
                    }else {
                        int pageNum = waitCount % pageSize == 0 ? (waitCount / pageSize) : ((waitCount / pageSize) + 1);
                        for (int i = 0; i < pageNum; i++) {
                            log.info("当前文本推送条数{}，偏移量为{}", pageSize,i*pageSize);
                            //获取活动明细表，条数大于一万条，然后分页查询推送
                            List<QywxActivityDetail> tmpUserList = qywxActivityPushMapper.getQywxUserList(headId,followUserId,y,pageSize,i * pageSize);
                            if(tmpUserList.size() > 0) {
                                //组织推送信息
                                QywxPushList qywxPushList=new QywxPushList();
                                qywxPushList.setTextContent(tmpUserList.get(0).getSmsContent());
                                qywxPushList.setMpTitle(tmpUserList.get(0).getMpTitle());
                                qywxPushList.setMpUrl(tmpUserList.get(0).getMpUrl());
                                qywxPushList.setMpMediaId(tmpUserList.get(0).getMpMediaId());
                                qywxPushList.setMpAppid(appId);
                                qywxPushList.setExternalContactIds(org.apache.commons.lang3.StringUtils.join(tmpUserList.stream().map(QywxActivityDetail::getQywxContractId).collect(Collectors.toList()),","));
                                qywxPushList.setFollowUserId(followUserId);
                                qywxPushList.setSourceId(tmpUserList.get(0).getQywxDetailId());
                                //将信息，放入uo_qywx_push_list表中。
                                qywxActivityPushMapper.insertPushList(qywxPushList);
                                //推送并更新状态
                                pushQywxMsg(qywxPushList,tmpUserList);
                            }
                        }
                    }
                });
            });

            //更新状态为 执行中(根据当前计划的状态更新不同的状态字段)
            qywxActivityHeadService.updateStatus(activityPlan.getHeadId(),
                    ActivityStatusEnum.DOING.getStatusCode(),
                    activityPlan.getPlanType());
        }
    }

    /**
     * 推送企业微信消息
     *
     * @param qywxPushList (待推送的对象)
     */
    public void pushQywxMsg(QywxPushList qywxPushList,List<QywxActivityDetail> qywxActivityDetailList) {

        if(null==qywxActivityDetailList||qywxActivityDetailList.size()==0){
            qywxActivityPushMapper.updatePushList(qywxPushList.getPushId(),"F","","","推送列表为空");
            return;
        }

        String msgContent = qywxPushList.getTextContent();
        String mpTitle = qywxPushList.getMpTitle();
        String mpUrl = qywxPushList.getMpUrl();
        String mediaId =qywxPushList.getMpMediaId();
        String appId = qywxPushList.getMpAppid();

        //判断文本或小程序至少有一个的变量
        boolean flag=true;
        QywxMessage qywxMessage = new QywxMessage();

        if (org.apache.commons.lang3.StringUtils.isNotEmpty(msgContent)) {
            qywxMessage.setText(msgContent);
            flag=false;
        }
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(mpTitle))
        {
            qywxMessage.setMpTitle(mpTitle);
            qywxMessage.setMpPicMediaId(mediaId);
            qywxMessage.setMpAppid(appId);
            qywxMessage.setMpPage(mpUrl);
            flag=false;
        }
        if(flag)
        {
            qywxActivityPushMapper.updatePushList(qywxPushList.getPushId(),"F","","","消息为空");
        }else {
            //获取对应企业微信客户ID集合
            List<String> contactIdList=qywxActivityDetailList.stream().map(QywxActivityDetail::getQywxContractId).collect(Collectors.toList());
            //调用企业微信接口，发送信息
            String result = qywxMessageService.pushQywxMessage(qywxMessage, qywxPushList.getFollowUserId(), contactIdList);
            log.info("日运营企微：推送结果【{}】", result);

            String status="S";
            String msgId ="";
            String failList="";
            String remark="推送成功";

            if(StringUtils.isEmpty(result))
            {
                status="F";
                remark="调用企业微信接口返回空";
            }else
            {
                JSONObject jsonObject = JSON.parseObject(result);
                msgId = jsonObject.getString("msgid");
                int errcode = jsonObject.getIntValue("errcode");
                failList = jsonObject.getString("fail_list");

                if(errcode!=0)
                {
                    status="F";
                    remark="调用企业微信接口失败";
                }
            }
            //更新状态。
            qywxActivityPushMapper.updatePushList(qywxPushList.getPushId(),status,msgId,failList,remark);

            List<Long> detailIdList=qywxActivityDetailList.stream().map(QywxActivityDetail::getQywxDetailId).collect(Collectors.toList());
            log.info("更新pushidpushid:{}",qywxPushList.getPushId());
            if(detailIdList.size()>0)
            {
                qywxActivityPushMapper.updatePushId(detailIdList,qywxPushList.getPushId(),msgId,status);
            }
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePlanToStop(QywxActivityPlan activityPlan) throws Exception{
        //更新推送计划的状态
        int count= qywxActivityPushMapper.updateStatus(activityPlan.getPlanId(),ActivityPlanStatusEnum.STOP.getStatusCode(),activityPlan.getVersion());

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
    public boolean validateSmsConfig(QywxActivityPlan activityPlan) {
        int count =qywxActivityPushMapper.validateSmsConfig(activityPlan.getHeadId(),activityPlan.getPlanType());
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
