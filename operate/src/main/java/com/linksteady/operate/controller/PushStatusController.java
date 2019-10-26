package com.linksteady.operate.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.service.DailyPropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hxcao
 * @date 2019-09-28
 */
@RestController
@RequestMapping("/push")
public class PushStatusController {

    @Autowired
    private DailyPropertiesService dailyPropertiesService;

    /**
     * 关闭推送服务
     * @param
     * @return
     */
    @GetMapping("/stop")
    public ResponseBo stop() {
        boolean flag = false;
        //判断当前状态
        DailyProperties dailyProperties = dailyPropertiesService.getDailyProperties();
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
        DailyProperties dailyProperties = dailyPropertiesService.getDailyProperties();
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
        DailyProperties dailyProperties = dailyPropertiesService.getDailyProperties();
        return ResponseBo.okWithData(null, dailyProperties.getPushFlag());
    }
}
