package com.linksteady.operate.thread;

import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.PushListLager;
import com.linksteady.operate.push.PushMessageService;
import com.linksteady.operate.service.SmsPushService;
import com.linksteady.operate.sms.montnets.domain.Message;
import com.linksteady.operate.util.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 双11活动批量发
 *
 * @author hxcao
 * @date 2019-10-23
 */
@Slf4j
public class BatchPushMessageThread extends Thread {

    /**
     * 批量接口的上限是1000
     */
    private final int PAGE_SIZE = 998;

    @Override
    public void run() {
        SmsPushService smsPushService = (SmsPushService) SpringContextUtils.getBean(SmsPushService.class);
        PushMessageService pushMessageService = (PushMessageService) SpringContextUtils.getBean("pushMessageServiceImpl");
        //推送对象
        DailyProperties dailyProperties = (DailyProperties) SpringContextUtils.getBean("dailyProperties");
        log.info(">>>对待发送的推送列表进行监控");
        while (true) {
            if (null != dailyProperties && "N".equals(dailyProperties.getPushFlag())) {
                log.info(">>>推送服务已通过配置停止");
                try {
                    //每隔1分钟执行一次
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            int currentHour = LocalDateTime.now().getHour();
            int count = smsPushService.getPushListCount(currentHour);
            if (count == 0) {
                log.info(">>>当前时间没有可推送的名单");
            } else {
                log.info(">>>当前时间获取到共{}条短信内容待推送", count);
                Long startTime = System.currentTimeMillis();
                Long maxPushId = smsPushService.getMaxPushId(currentHour);
                List<String> smsContent = smsPushService.getSmsContent(currentHour);
                // 迭代发送每种短信的用户名单
                for (String sms : smsContent) {
                    // 根据短信内容和当前时间获取数据总量
                    int total = smsPushService.getPushListCountBySms(currentHour, sms);
                    log.info(">>>短信内容：{},推送名单数：{}", sms, total);
                    int pageSize = PAGE_SIZE;
                    int pageNum = total % pageSize == 0 ? total / pageSize : (total / pageSize) + 1;
                    for (int i = 0; i < pageNum; i++) {
                        int start = i * pageSize;
                        int end = (i + 1) * pageSize - 1;
                        end = end > total ? total : end;
                        List<PushListLager> pushList = smsPushService.getPushList(currentHour, sms, start, end);
                        // 封装message调用批量推送接口即可。
                        String mobile = pushList.stream().map(PushListLager::getUserPhone).collect(Collectors.joining(","));
                        Message message = new Message();
                        message.setContent(sms);
                        message.setMobile(mobile);
                        pushMessageService.batchPush(message);
                        log.info(">>>当前推送第{}页名单", sms, i);
                    }
                    log.info(">>>短信内容：{}推送完毕", sms);
                }
                Long endTime = System.currentTimeMillis();
                log.info(">>>本次推送完毕，耗时{}秒", (endTime - startTime)/1000);
                log.info(">>>开始更新推送状态");
                smsPushService.updatePushState(smsContent, maxPushId);
                log.info(">>>结束更新推送状态");
            }
            //每隔5分钟执行一次
            try {
                TimeUnit.MINUTES.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info(">>>开始下一次推送");
        }
    }
}
