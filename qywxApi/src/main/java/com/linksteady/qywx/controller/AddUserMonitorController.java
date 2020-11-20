package com.linksteady.qywx.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.service.AddUserMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 企业微信拉新效果&监控展示
 * @author hxcao
 * @date 2020/8/15
 */
@RestController
@RequestMapping("/addUserMonitor")
public class AddUserMonitorController {

    @Autowired
    private AddUserMonitorService addUserMonitorService;
    /**
     * 企业微信通过申请人数的变化趋势
     * @return
     */
    @RequestMapping("/getApplySuccessData")
    public ResponseBo getApplySuccessData(String startDt, String endDt, String dateType) {
        return ResponseBo.okWithData(null, addUserMonitorService.getApplySuccessData(startDt, endDt, dateType));
    }

    /**
     * 不同添加方式下的转化人数和转化率
     * @param startDt
     * @param endDt
     * @param dateType
     * @return
     */
    @RequestMapping("/getConvertCntAndRate")
    public ResponseBo getConvertCntAndRate(String startDt, String endDt, String dateType) {
        return ResponseBo.okWithData(null, addUserMonitorService.getConvertCntAndRate(startDt, endDt, dateType));
    }
}
