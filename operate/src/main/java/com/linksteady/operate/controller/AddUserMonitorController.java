package com.linksteady.operate.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 企业微信拉新效果&监控展示
 * @author hxcao
 * @date 2020/8/15
 */
@RestController
@RequestMapping("/addUserMonitor")
public class AddUserMonitorController {


    /**
     * 获取连续的时间列表
     * @param startDt
     * @param endDt
     * @param dateType
     * @return
     */
    private List<String> getDateList(String startDt, String endDt, String dateType) {
        if("Y".equalsIgnoreCase(dateType)) {
            LocalDate start = LocalDate.parse(startDt, DateTimeFormatter.ofPattern("yyyy"));
            LocalDate end = LocalDate.parse(startDt, DateTimeFormatter.ofPattern("yyyy"));
            while (end.isAfter(start)) {

            }
        }
        return null;
    }

}
