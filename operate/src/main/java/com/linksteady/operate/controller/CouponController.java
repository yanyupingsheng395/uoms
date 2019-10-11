package com.linksteady.operate.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.CouponInfo;
import com.linksteady.operate.service.impl.CouponServiceImpl;
import com.linksteady.operate.service.impl.ShortUrlServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * 短信相关的controller
 *
 * @author huang
 */
@RestController
@RequestMapping("/coupon")
@Slf4j
public class CouponController extends BaseController {

    @Autowired
    CouponServiceImpl couponService;


    @Autowired
    ShortUrlServiceImpl shortUrlService;

    /**
     * 获取短信模板
     *
     * @param
     * @return
     */
    @RequestMapping("/list")
    public ResponseBo smsTemplateList(@RequestBody QueryRequest request) {
        List<CouponInfo> result = couponService.getList((request.getPageNum() - 1) * request.getPageSize() + 1, request.getPageNum() * request.getPageSize());
        int totalCount = couponService.getTotalCount();
        return ResponseBo.okOverPaging("", totalCount, result);
    }

    /**
     * 获取某个groupId下的couponIds
     *
     * @param groupId
     * @return
     */
    @RequestMapping("/getCouponIdsByGroupId")
    public ResponseBo getCouponIdsByGroupId(String groupId) {
        List<Integer> ids = couponService.getCouponIdsByGroupId(groupId);
        return ResponseBo.okWithData(null, ids);
    }

    /**
     * 根据组ID更新couponId
     *
     * @param groupId
     * @param couponId
     * @return
     */
    @RequestMapping("/updateCouponId")
    public ResponseBo updateCouponId(String groupId, String couponId) {
        couponService.updateCouponId(groupId, couponId);
        return ResponseBo.ok();
    }

    @RequestMapping("/save")
    public ResponseBo save(CouponInfo couponInfo) {
        couponService.save(couponInfo);
        return ResponseBo.ok();
    }

    @RequestMapping("/update")
    public ResponseBo update(CouponInfo couponInfo) {
        couponService.update(couponInfo);
        return ResponseBo.ok();
    }

    /**
     * 根据couponId获取记录
     *
     * @param couponId
     * @return
     */
    @RequestMapping("/getByCouponId")
    public ResponseBo getByCouponId(String couponId) {
        int count = couponService.isCouponUsed(couponId);
        if (count != 0) {
            return ResponseBo.error("该优惠券正在使用，无法修改！");
        }
        CouponInfo couponInfo = couponService.getByCouponId(couponId);
        return ResponseBo.okWithData(null, couponInfo);
    }

    /**
     * 删除记录
     *
     * @param couponId
     * @return
     */
    @RequestMapping("/deleteByCouponId")
    public ResponseBo deleteByCouponId(String couponId) {
        int count = couponService.isCouponUsed(couponId);
        if (count != 0) {
            return ResponseBo.error("该优惠券正在使用，无法删除！");
        }
        couponService.updateStatus(couponId);
        return ResponseBo.ok();
    }

    /**
     * 获取短链
     * @return
     */
    @RequestMapping("/getShortUrl")
    public ResponseBo getShortUrl(String url) {
        String shortUrl;
        if (StringUtils.isNotEmpty(url)) {
            shortUrl = shortUrlService.produceShortUrlByBaidu(url);
            if("".equalsIgnoreCase(shortUrl)) {
                return ResponseBo.error("长链地址不合法！");
            }
        }else {
            return ResponseBo.error("长链地址不合法！");
        }
        return ResponseBo.okWithData(null, shortUrl);
    }

    /**
     * 删除组-券关系
     * @param id
     * @return
     */
    @RequestMapping("/deleteCoupon")
    public ResponseBo deleteCoupon(String id) {
        if(StringUtils.isNotEmpty(id)) {
            List<String> ids = Arrays.asList(id.split(","));
            couponService.deleteCoupon(ids);
        }else {
            return ResponseBo.error();
        }
        return ResponseBo.ok();
    }
}

