package com.linksteady.wxofficial.controller;

import com.linksteady.common.domain.ResponseBo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hxcao
 * @date 2020/5/8
 */
@RestController
@RequestMapping("/dailyConfig")
public class DailyConfigController {

    @GetMapping("/getDataList")
    public ResponseBo getDataList() {
        return ResponseBo.ok();
    }
}
