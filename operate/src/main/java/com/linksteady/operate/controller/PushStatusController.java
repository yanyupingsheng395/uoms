package com.linksteady.operate.controller;

import com.google.common.collect.Maps;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.service.DailyPropertiesService;
import com.linksteady.operate.service.PushListService;
import com.linksteady.operate.service.PushLogService;
import com.linksteady.operate.service.impl.RedisMessageServiceImpl;
import com.linksteady.operate.thread.MonitorThread;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2019-09-28
 */
@RestController
@RequestMapping("/push")
public class PushStatusController extends BaseController {

    @Autowired
    private DailyProperties dailyProperties;

    @Autowired
    private DailyPropertiesService dailyPropertiesService;

    @Autowired
    private PushLogService pushLogService;

    @Autowired
    private PushListService pushListService;

    @Autowired
    private RedisMessageServiceImpl redisMessageService;

    /**
     * 关闭推送服务
     * @param
     * @return
     */
    @GetMapping("/stop")
    public ResponseBo stop() {
        boolean flag = false;
        //判断当前状态
        if(null != dailyProperties && dailyProperties.getPushFlag().equalsIgnoreCase("Y")) {
            dailyProperties.setPushFlag("N");
            dailyPropertiesService.updateProperties(dailyProperties);
            dailyPropertiesService.sendPushSignal("stop");
            flag = true;
        }
        if(flag) {
            return ResponseBo.ok("关闭服务成功！");
        }
        return ResponseBo.error("关闭服务失败！");
    }

    /**
     * 开启推送服务
     * @param
     * @return
     */
    @GetMapping("/start")
    public ResponseBo start() {
        boolean flag = false;
        //判断当前状态
        if(null != dailyProperties && dailyProperties.getPushFlag().equalsIgnoreCase("N")) {
            dailyProperties.setPushFlag("Y");
            dailyPropertiesService.updateProperties(dailyProperties);
            dailyPropertiesService.sendPushSignal("start");
            flag = true;
        }
        if(flag) {
            return ResponseBo.ok("开启服务成功！");
        }
        return ResponseBo.error("开启服务失败！");
    }

    /**
     * 获取当前服务的启动状态
     * @param
     * @return
     */
    @GetMapping("/status")
    public ResponseBo status() {
        return ResponseBo.okWithData(null, dailyProperties.getPushFlag());
    }

    /**
     * 获取推送的日志
     * @param
     * @return
     */
    @GetMapping("/getPushLog")
    public ResponseBo getPushLog(@RequestParam("day") int day) {
        MonitorThread monitorThread=MonitorThread.getInstance();

        List<PushLog> list=pushLogService.getPushLogList(day);
        //分成两部分
        List<PushLog> pushLogList= list.stream().filter(p->"1".equals(p.getLogType())&&p.getUserCount()>0).collect(Collectors.toList());
        List<PushLog> repeatLogList= list.stream().filter(p->"0".equals(p.getLogType())&&p.getUserCount()>0).collect(Collectors.toList());

        List<PushLog> purgeLogList= list.stream().filter(p->"2".equals(p.getLogType())).collect(Collectors.toList());
        Map<String,Object> map= Maps.newHashMap();
        map.put("push",pushLogList);
        map.put("repeat",repeatLogList);
        map.put("purge",purgeLogList);
        map.put("logDate", LocalDate.now().minusDays(day).format(DateTimeFormatter.ofPattern("MM-dd")));
        map.put("lastPushDate", null==monitorThread.getLastPushDate()?"":monitorThread.getLastPushDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        map.put("lastBatchPushDate",null==monitorThread.getLastBatchPushDate()?"":monitorThread.getLastBatchPushDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        map.put("lastPurgeDate",null==monitorThread.getLastPurgeDate()?"":monitorThread.getLastPurgeDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return ResponseBo.okWithData("",map);
    }

    @GetMapping("/getPushInfoListPage")
    public ResponseBo getPushInfoListPage(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String sourceCode = request.getParam().get("sourceCode");
        String pushStatus = request.getParam().get("pushStatus");
        String pushDateStr = request.getParam().get("pushDateStr");
        List<PushListInfo> dataList = pushListService.getPushInfoListPage(start, end, sourceCode, pushStatus, pushDateStr);
        int count = pushListService.getTotalCount(sourceCode, pushStatus, pushDateStr);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 获取每日运营配置信息
     * @param
     * @return
     */
    @GetMapping("/getDailyProperties")
    public ResponseBo getDailyProperties() {
        return ResponseBo.okWithData("",dailyPropertiesService.getDailyProperties());
    }

    /**
     * 更新每日运营配置信息  属性值目前只支持String和int两种类型
     */
    @PostMapping("/updateDailyProperties")
    @SneakyThrows
    public ResponseBo updateDailyProperties(@RequestBody DailyProperties dp) {
        dailyProperties.setPushFlag(dp.getPushFlag());
        dailyProperties.setAlertPhone(dp.getAlertPhone());
        dailyProperties.setCurrentUser(getCurrentUser().getUsername());
        //更新到数据库中
        dailyPropertiesService.updateProperties(dailyProperties);
        return ResponseBo.okWithData("",dailyPropertiesService.getDailyProperties());
    }

    /**
     * 刷新每日运营配置信息 (将数据库中的信息同步到内存的对象中)
     * @param
     * @return
     */
    @GetMapping("/refreshDailyProperties")
    public ResponseBo refreshDailyProperties() {
        DailyProperties temp=dailyPropertiesService.getDailyProperties();

        dailyProperties.setPushFlag(temp.getPushFlag());
        dailyProperties.setRepeatPushDays(temp.getRepeatPushDays());
        dailyProperties.setStatsDays(temp.getStatsDays());
        dailyProperties.setPushType(temp.getPushType());
        dailyProperties.setOpenAlert(temp.getOpenAlert());
        dailyProperties.setAlertPhone(temp.getAlertPhone());
        dailyProperties.setPushMethod(temp.getPushMethod());
        dailyProperties.setSmsLengthLimit(temp.getSmsLengthLimit());
        dailyProperties.setProductUrl(temp.getProductUrl());
        dailyProperties.setIsTestEnv(temp.getIsTestEnv());
        dailyProperties.setDemoShortUrl(temp.getDemoShortUrl());
        dailyProperties.setProdNameLen(temp.getProdNameLen());
        dailyProperties.setShortUrlLen(temp.getShortUrlLen());
        dailyProperties.setCouponSendType(temp.getCouponSendType());
        dailyProperties.setCouponNameLen(temp.getCouponNameLen());

        //通过redis消息通知pushserver
        HeartBeatInfo heartBeatInfo = new HeartBeatInfo();
        heartBeatInfo.setStartOrStop("refresh");
        redisMessageService.sendPushSingal(heartBeatInfo);

        return ResponseBo.okWithData("",dailyPropertiesService.getDailyProperties());
    }
}
