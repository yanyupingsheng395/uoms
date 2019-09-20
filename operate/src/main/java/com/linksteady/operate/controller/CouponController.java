package com.linksteady.operate.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.CouponInfo;
import com.linksteady.operate.service.impl.CouponServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 短信相关的controller
 * @author huang
 */
@RestController
@RequestMapping("/coupon")
@Slf4j
public class CouponController extends BaseController {

    @Autowired
    CouponServiceImpl couponService;

    /**
     * 获取短信模板
     * @param
     * @return
     */
    @RequestMapping("/list")
    public ResponseBo smsTemplateList(@RequestBody QueryRequest request) {
        List<CouponInfo> result=couponService.getList((request.getPageNum()-1)*request.getPageSize()+1, request.getPageNum()*request.getPageSize());
        int totalCount= couponService.getTotalCount();
        return  ResponseBo.okOverPaging("",totalCount,result);
    }

}

