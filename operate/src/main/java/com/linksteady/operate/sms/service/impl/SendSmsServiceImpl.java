package com.linksteady.operate.sms.service.impl;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.linksteady.operate.dao.DailyDetailMapper;
import com.linksteady.operate.dao.DailyPushMapper;
import com.linksteady.operate.sms.domain.SmsInfo;
import com.linksteady.operate.sms.domain.TaskInfo;
import com.linksteady.operate.sms.montnets.config.ConfigManager;
import com.linksteady.operate.sms.montnets.domain.Message;
import com.linksteady.operate.sms.montnets.domain.MultiMt;
import com.linksteady.operate.sms.montnets.domain.Remains;
import com.linksteady.operate.sms.montnets.send.SendSms;
import com.linksteady.operate.sms.service.SendSmsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author hxcao
 * @date 2019-09-19
 */
@Slf4j
@Service
public class SendSmsServiceImpl implements SendSmsService {

    private final String SEND_TYPE_SINGLE = "single";
    private final String SEND_TYPE_BATCH = "batch";
    private final String SEND_TYPE_MULTI = "multi";

    private final int MAX_MSG_COUNT = 100;

    final static ExecutorService threadPool = Executors.newFixedThreadPool(4, new ThreadFactoryBuilder().setNameFormat("send-msg-pool-%d").build());
    final static ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(4, new ThreadFactoryBuilder().setNameFormat("send-msg-pool-%d").build());

    @Value("${sms.montnets.userid}")
    private String userid;

    @Value("${sms.montnets.pwd}")
    private String pwd;

    @Value("${sms.montnets.masterIpAddress}")
    private String masterIpAddress;

    private boolean isEncryptPwd = ConfigManager.IS_ENCRYPT_PWD;

    @Autowired
    private DailyPushMapper dailyPushMapper;

    @Override
    public Map<String, Object> getInfo() {

        ConfigManager.setIpInfo(masterIpAddress, null, null, null);
        ConfigManager.IS_ENCRYPT_PWD = true;

        Map<String, Object> result = Maps.newHashMap();
        SendSms sendSms = new SendSms(userid, pwd, isEncryptPwd, masterIpAddress, null, null, null);
        Remains remain = sendSms.getRemains(new Message());
        result.put("userid", userid);
        result.put("balance", remain.getBalance());
        result.put("money", remain.getMoney());
        return result;
    }


    /**
     * 立即发送
     *
     * @param taskInfo 待执行的任务信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendMsg(TaskInfo taskInfo) {
        String taskId = taskInfo.getTaskId();
        String runType = taskInfo.getRunType();
        String sendType = taskInfo.getSendMsgType();
        List<SmsInfo> smsInfos = taskInfo.getSmsInfoList();

        final String RUN_TYPE_NOW = "now";
        if (RUN_TYPE_NOW.equalsIgnoreCase(runType)) {
            if (smsInfos.size() < MAX_MSG_COUNT) {
                sendMsgList(sendType, smsInfos);
            } else {
                int count = smsInfos.size();
                int totalPage = count % MAX_MSG_COUNT == 0 ? count / MAX_MSG_COUNT : count / MAX_MSG_COUNT + 1;
                for (int i = 0; i < totalPage; i++) {
                    int start = i * MAX_MSG_COUNT + 1;
                    int end = (i + 1) * MAX_MSG_COUNT;
                    List<SmsInfo> subList = smsInfos.subList(start, end);
                    sendMsgList(sendType, subList);
                }
            }
        }

        final String RUN_TYPE_SCHEDULED = "scheduled";
        if (RUN_TYPE_SCHEDULED.equalsIgnoreCase(runType)) {
            if (smsInfos.size() < MAX_MSG_COUNT) {
                sendMsgListScheduled(taskId, sendType, smsInfos);
            } else {
                int count = smsInfos.size();
                int totalPage = count % MAX_MSG_COUNT == 0 ? count / MAX_MSG_COUNT : count / MAX_MSG_COUNT + 1;
                for (int i = 0; i < totalPage; i++) {
                    int start = i * MAX_MSG_COUNT + 1;
                    int end = (i + 1) * MAX_MSG_COUNT;
                    List<SmsInfo> subList = smsInfos.subList(start, end);
                    sendMsgListScheduled(taskId,sendType, subList);
                }
            }
        }
    }

    /**
     * 根据发送类型发送数据
     *
     * @param sendType
     * @param smsInfos
     */
    private void sendMsgList(String sendType, List<SmsInfo> smsInfos) {
        SendSms sendSms = new SendSms(userid, pwd, isEncryptPwd, masterIpAddress,null,null,null);
        // todo 通过关联数据查询list
        if (SEND_TYPE_SINGLE.equalsIgnoreCase(sendType)) {
            Future<List<SmsInfo>> future = threadPool.submit(() -> {
                smsInfos.forEach(x -> {
                    Message message = new Message();
                    message.setMobile(x.getMobile());
                    message.setContent(x.getContent());
                    int result = sendSms.singleSend(message);
                    x.setStatus(Integer.valueOf(result).toString());
                });
                return smsInfos;
            });
            // todo future.get() 到list数据进行smsInfo和taskInfo字段的更新
        }

        if (SEND_TYPE_BATCH.equalsIgnoreCase(sendType)) {
            Message message = new Message();
            String mobiles;
            List<String> mobileList = Lists.newArrayList();
            smsInfos.stream().forEach(x -> {
                mobileList.add(x.getMobile());
            });
            String[] tmp = new String[mobileList.size()];
            mobileList.toArray(tmp);
            mobiles = StringUtils.join(tmp, ",");
            message.setMobile(mobiles);
            message.setContent(smsInfos.get(0).getContent());

            Future<List<SmsInfo>> future = threadPool.submit(() -> {
                int result = sendSms.batchSend(message);
                smsInfos.stream().forEach(x -> x.setStatus(String.valueOf(result)));
                return smsInfos;
            });

            // todo do update taskInfo, smsInfo
        }

        if (SEND_TYPE_MULTI.equalsIgnoreCase(sendType)) {
            List<MultiMt> multiMts = Lists.newArrayList();
            smsInfos.forEach(x -> {
                MultiMt multiMt = new MultiMt();
                multiMt.setMobile(x.getMobile());
                multiMt.setContent(x.getContent());
                multiMts.add(multiMt);
            });

            Future<List<SmsInfo>> future = threadPool.submit(() -> {
                int result = sendSms.multiSend(multiMts, new Message());
                smsInfos.stream().forEach(x -> x.setStatus(String.valueOf(result)));
                return smsInfos;
            });
            // todo do update taskInfo, smsInfo
        }
    }

    /**
     * 根据触达时段进行触达
     *
     * @param sendType
     * @param smsInfos
     */
    private void sendMsgListScheduled(String taskId, String sendType, List<SmsInfo> smsInfos) {
        SendSms sendSms = new SendSms(userid, pwd, isEncryptPwd, masterIpAddress,null,null,null);
        Map<String, List<SmsInfo>> tmpMap = smsInfos.stream().collect(Collectors.groupingBy(SmsInfo::getTouchTime));
        threadPool.submit(()->{
            if (SEND_TYPE_SINGLE.equalsIgnoreCase(sendType)) {
                log.info(">>>正在使用单条推送方式推送短信.");
                tmpMap.entrySet().stream().forEach(x -> {
                    List<SmsInfo> tmpList = x.getValue();
                    log.info(">>>正在推送{}个用户。", tmpList.size());
                    final ScheduledFuture<List<SmsInfo>> schedule = scheduledThreadPool.schedule(() -> {
                        tmpList.stream().forEach(v -> {
                            Message message = new Message();
                            message.setMobile(v.getMobile());
                            message.setContent(v.getContent());
                            int result = sendSms.singleSend(message);
                            v.setStatus(String.valueOf(result));
                        });
                        return tmpList;
                    }, getDelay(Integer.valueOf(x.getKey())), TimeUnit.MINUTES);
                    try {
                        final List<SmsInfo> smsInfoList = schedule.get();
                        log.info(">>>推送完毕[{}:00]的用户。", LocalTime.now().format(DateTimeFormatter.ofPattern("HH")));
                        updateSmsInfoStatus(smsInfoList, taskId);
                    } catch (InterruptedException e) {
                        log.error(">>>推送用户短信发生异常", e);
                    } catch (ExecutionException e) {
                        log.error(">>>推送用户短信发生异常", e);
                    }
                    // todo future.get() 到list数据进行smsInfo和taskInfo字段的更新
                });
            }
            if (SEND_TYPE_BATCH.equalsIgnoreCase(sendType)) {
                tmpMap.entrySet().stream().forEach(x -> {
                    Message message = new Message();
                    String mobiles;
                    List<String> mobileList = Lists.newArrayList();
                    x.getValue().stream().forEach(v -> {
                        mobileList.add(v.getMobile());
                    });
                    String[] tmp = new String[mobileList.size()];
                    mobileList.toArray(tmp);
                    mobiles = StringUtils.join(tmp, ",");
                    message.setMobile(mobiles);
                    message.setContent(x.getValue().get(0).getContent());
                    ScheduledFuture<List<SmsInfo>> schedule = scheduledThreadPool.schedule(() -> {
                        int result = sendSms.batchSend(message);
                        x.getValue().stream().forEach(v -> v.setStatus(String.valueOf(result)));
                        return smsInfos;
                    }, getDelay(Integer.valueOf(x.getKey())), TimeUnit.MINUTES);
                });
                // todo do update taskInfo, smsInfo
            }

            if (SEND_TYPE_MULTI.equalsIgnoreCase(sendType)) {
                log.info(">>>使用MULTI方式发送短信.");
                tmpMap.entrySet().stream().forEach(x -> {
                    List<MultiMt> multiMts = Lists.newArrayList();
                    x.getValue().stream().forEach(v -> {
                        MultiMt multiMt = new MultiMt();
                        multiMt.setMobile(v.getMobile());
                        multiMt.setContent(v.getContent());
                        multiMts.add(multiMt);
                    });

                    final ScheduledFuture<List<SmsInfo>> schedule = scheduledThreadPool.schedule(() -> {
                        int result = sendSms.multiSend(multiMts, new Message());
                        x.getValue().stream().forEach(v -> v.setStatus(String.valueOf(result)));
                        return smsInfos;
                    }, getDelay(Integer.valueOf(x.getKey())), TimeUnit.MINUTES);
                    // todo do update taskInfo, smsInfo
                });
            }
        });
    }
    /**
     * 获取当前时间和触达时间的时间差
     *
     * @param hour
     * @return
     */
    private Long getDelay(int hour) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime later = LocalDateTime.now().withHour(hour).withMinute(0);
        Duration duration = Duration.between(now, later);
        Long delay = duration.toMinutes();
        return delay;
    }

    public void stopSendMsg() {
        threadPool.shutdownNow();
    }

    private void updateSmsInfoStatus(List<SmsInfo> smsInfos, String taskId) {
        log.info("正在更新短信发送状态...");
        dailyPushMapper.updateSendMsgStatus(smsInfos, taskId);
        log.info("短信发送状态更新完毕.");
    }
}
