package com.linksteady.operate.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.QywxWelcomeCoupon;
import com.linksteady.operate.domain.QywxWelcomeProduct;
import com.linksteady.operate.service.QywxWelcomeCouponService;
import com.linksteady.operate.service.QywxWelcomeProductService;
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
    private QywxWelcomeCouponService qywxWelcomeCouponService;

    @GetMapping("/getTableDataList")
    public ResponseBo getTableDataList(Integer limit, Integer offset) {
        int count = qywxWelcomeCouponService.getTableDataCount();
        List<QywxWelcomeCoupon> dataList = qywxWelcomeCouponService.getTableDataList(limit, offset);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    @PostMapping("/saveData")
    public ResponseBo saveData(QywxWelcomeCoupon qywxWelcomeCoupon) {
        qywxWelcomeCouponService.saveData(qywxWelcomeCoupon);
        return ResponseBo.ok();
    }

    @GetMapping("/getDataById")
    public ResponseBo getDataById(String couponId) {
        return ResponseBo.okWithData(null, qywxWelcomeCouponService.getDataById(couponId));
    }

    @PostMapping("/updateData")
    public ResponseBo updateData(QywxWelcomeCoupon qywxWelcomeCoupon) {
        qywxWelcomeCouponService.updateData(qywxWelcomeCoupon);
        return ResponseBo.ok();
    }

    @PostMapping("/deleteCouponById")
    public ResponseBo deleteCouponById(String couponId) {
        qywxWelcomeCouponService.deleteCouponById(couponId);
        return ResponseBo.ok();
    }
}
