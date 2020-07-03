package com.linksteady.qywx.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.service.OrderSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class OrderSeriesController extends BaseController{

    @Autowired
    OrderSeriesService orderSeriesService;
    /**
     * 获取当前用户的购买时序数据
     * @return
     */
    @GetMapping("/getOrderSeries")
    public ResponseBo getOrderSeries(@RequestParam String userId) {
        return ResponseBo.okWithData(null, orderSeriesService.getOrderData(userId));
    }

}
