package com.linksteady.operate.controller;

import com.google.common.collect.Maps;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.PushLog;
import com.linksteady.operate.service.DailyPropertiesService;
import com.linksteady.operate.service.PushLogService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
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

    @Autowired
    private PushLogService pushLogService;


    /**
     * 获取每日运营配置信息
     * @param
     * @return
     */
    @GetMapping("/getDailyProperties")
    public ResponseBo getPageList() {
        return ResponseBo.okWithData("",dailyPropertiesService.getDailyProperties());
    }

    /**
     * 更新每日运营配置信息  属性值目前只支持String和int两种类型
     */
    @PostMapping("/updateDailyProperties")
    @SneakyThrows
    public ResponseBo updateDailyProperties(@RequestBody DailyProperties dp) {
        dailyProperties.setPushFlag(dp.getPushFlag());
        dailyProperties.setRepeatPushDays(dp.getRepeatPushDays());
        dailyProperties.setStatsDays(dp.getStatsDays());
        dailyProperties.setPushType(dp.getPushType());
        dailyProperties.setOpenAlert(dp.getOpenAlert());
        dailyProperties.setAlertPhone(dp.getAlertPhone());

        dailyProperties.setPushMethod(dp.getPushMethod());
        dailyProperties.setCouponMthod(dp.getCouponMthod());
        dailyProperties.setCouponUrlToShort(dp.getCouponUrlToShort());
        dailyProperties.setIncludeProdUrl(dp.getIncludeProdUrl());
        dailyProperties.setProdUrlToShort(dp.getProdUrlToShort());
        dailyProperties.setSmsLengthLimit(dp.getSmsLengthLimit());

        dailyProperties.setCurrentUser(getCurrentUser().getUsername());
        //更新属性
        dailyPropertiesService.updateProperties(dailyProperties);

        return ResponseBo.okWithData("",dailyPropertiesService.getDailyProperties());
    }

    /**
     * 获取推送的日志
     * @param
     * @return
     */
    @GetMapping("/getPushLog")
    public ResponseBo getPushLog(int day) {
        if(day==0)
        {
            day=1;
        }
        List<PushLog> list=pushLogService.getPushLogList(day);

        //分成两部分
        List<PushLog> pushLogList= list.stream().filter(p->"1".equals(p.getLogType())).collect(Collectors.toList());

        List<PushLog> repeatLogList= list.stream().filter(p->"2".equals(p.getLogType())).collect(Collectors.toList());

        Map<String,List<PushLog>> map= Maps.newHashMap();
        map.put("push",pushLogList);
        map.put("repeat",repeatLogList);

        return ResponseBo.okWithData("",map);
    }

    /**
     * 关闭推送服务
     * @param
     * @return
     */
    @GetMapping("/stop")
    public ResponseBo stop() {
        //判断当前状态

        return ResponseBo.okWithData("",dailyPropertiesService.getDailyProperties());
    }

}
