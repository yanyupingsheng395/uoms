package com.linksteady.operate.controller;

import com.google.common.collect.Maps;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.PushLog;
import com.linksteady.operate.service.DailyPropertiesService;
import com.linksteady.operate.service.PushLogService;
import com.linksteady.operate.thread.MonitorThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
public class PushStatusController {

    @Autowired
    private DailyProperties dailyProperties;

    @Autowired
    private DailyPropertiesService dailyPropertiesService;

    @Autowired
    private PushLogService pushLogService;

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
}
