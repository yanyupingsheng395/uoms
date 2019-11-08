package com.linksteady.operate.controller;

import com.google.common.collect.Maps;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.PushLog;
import com.linksteady.operate.service.DailyPropertiesService;
import com.linksteady.operate.service.PushLogService;
import com.linksteady.operate.thread.MonitorThread;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 每日运营配置信息维护
 * @author hxcao
 * @date 2019-07-31
 */
@RestController
@RequestMapping("/dailyConfig")
public class PushConfigController extends BaseController {

    @Autowired
    private DailyPropertiesService dailyPropertiesService;

    @Autowired
    private DailyProperties dailyProperties;


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
        dailyProperties.setIsAliApp(temp.getIsAliApp());
        dailyProperties.setIsTestEnv(temp.getIsTestEnv());
        dailyProperties.setDemoShortUrl(temp.getDemoShortUrl());
        dailyProperties.setProdNameLen(temp.getProdNameLen());
        dailyProperties.setShortUrlLen(temp.getShortUrlLen());

        return ResponseBo.okWithData("",dailyPropertiesService.getDailyProperties());
    }

}
