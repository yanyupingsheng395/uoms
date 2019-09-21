package com.linksteady.operate.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.service.DailyPropertiesService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;

/**
 * 每日运营配置信息维护
 * @author hxcao
 * @date 2019-07-31
 */
@RestController
@RequestMapping("/dailyConfig")
public class DailyPropertiesController {

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
        dailyProperties.setRepeatPush(dp.getRepeatPush());
        dailyProperties.setRepeatPushDays(dp.getRepeatPushDays());
        dailyProperties.setStatsDays(dp.getStatsDays());
        dailyProperties.setPushType(dp.getPushType());
        dailyProperties.setOpenAlert(dp.getOpenAlert());
        dailyProperties.setAlertPhone(dp.getAlertPhone());

        //更新属性
        dailyPropertiesService.updateProperties(dailyProperties);

        return ResponseBo.okWithData("",dailyPropertiesService.getDailyProperties());
    }
}
