package com.linksteady.operate.controller;

import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.PushListInfo;
import com.linksteady.operate.service.PushListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-09-28
 */
@RestController
@RequestMapping("/push")
public class PushStatusController {

    @Autowired
    private DailyProperties dailyProperties;

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
