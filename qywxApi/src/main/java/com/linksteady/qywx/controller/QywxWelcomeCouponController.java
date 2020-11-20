package com.linksteady.qywx.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.domain.QywxWelcomeCoupon;
import com.linksteady.qywx.service.WelcomeCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/9/8
 */
@RestController
@RequestMapping("/qywxWelcomeCoupon")
public class QywxWelcomeCouponController {

    @Autowired
    private WelcomeCouponService welcomeCouponService;

    @GetMapping("/getTableDataList")
    public ResponseBo getTableDataList(Integer limit, Integer offset) {
        int count = welcomeCouponService.getTableDataCount();
        List<QywxWelcomeCoupon> dataList = welcomeCouponService.getTableDataList(limit, offset);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    @PostMapping("/saveData")
    public ResponseBo saveData(QywxWelcomeCoupon qywxWelcomeCoupon) {
        welcomeCouponService.saveData(qywxWelcomeCoupon);
        return ResponseBo.ok();
    }

    @GetMapping("/getDataById")
    public ResponseBo getDataById(String couponId) {
        return ResponseBo.okWithData(null, welcomeCouponService.getDataById(couponId));
    }

    @PostMapping("/updateData")
    public ResponseBo updateData(QywxWelcomeCoupon qywxWelcomeCoupon) {
        welcomeCouponService.updateData(qywxWelcomeCoupon);
        return ResponseBo.ok();
    }

    @PostMapping("/deleteCouponById")
    public ResponseBo deleteCouponById(String couponId) {
        welcomeCouponService.deleteCouponById(couponId);
        return ResponseBo.ok();
    }
}
