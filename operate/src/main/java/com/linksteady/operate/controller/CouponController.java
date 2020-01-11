package com.linksteady.operate.controller;

import com.google.common.collect.Lists;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.CouponInfo;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.service.impl.CouponServiceImpl;
import com.linksteady.operate.service.impl.ShortUrlServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private DailyProperties dailyProperties;

    /**
     * 获取券list
     *
     * @param
     * @return
     */
    @RequestMapping("/list")
    public ResponseBo smsTemplateList(@RequestBody QueryRequest request) {
        String validStatus = new String();
        if(null != request.getParam()) {
            validStatus = request.getParam().get("validStatus");
        }
        List<CouponInfo> result = couponService.getList((request.getPageNum() - 1) * request.getPageSize() + 1, request.getPageNum() * request.getPageSize(), validStatus);
        int totalCount = couponService.getTotalCount(validStatus);
        return ResponseBo.okOverPaging("", totalCount, result);
    }

    /**
     * 根据群组信息过滤优惠券
     * @return
     */
    @GetMapping("/getCouponList")
    public ResponseBo getCouponList(@RequestParam("groupId") String groupId) {
        return ResponseBo.okWithData(null, couponService.getCouponList(groupId));
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
        couponInfo.setCouponSource("1");
        couponInfo = getCheckInfo(couponInfo);
        couponService.save(couponInfo);
        return ResponseBo.ok();
    }

    /**
     * 对优惠券的验证
     * 1.券链接是否配置
     * 2.适用人群是否配置
     * 3.券设置了失效
     * @param couponInfo
     * @return
     */
    private CouponInfo getCheckInfo(CouponInfo couponInfo) {
        // 默认为验证通过
        couponInfo.setCheckFlag("1");
        String couponUrl = couponInfo.getCouponUrl();
        // 链接为空
        if(StringUtils.isEmpty(couponUrl)) {
            couponInfo.setCheckFlag("0");
            couponInfo.setCheckComments("补贴链接没有配置");
        }
        // 判断用户价值，可以不配置，后期进行补齐
        if(StringUtils.isEmpty(couponInfo.getUserValue()) || StringUtils.isEmpty(couponInfo.getLifeCycle()) || StringUtils.isEmpty(couponInfo.getPathActive())) {
            couponInfo.setCheckFlag("0");
            couponInfo.setCheckComments("适用人群没有完整配置");
        }
        // 券的有效状态
        String validStatus = couponInfo.getValidStatus();
        if(StringUtils.isNotEmpty(validStatus) && validStatus.equalsIgnoreCase("N")) {
            couponInfo.setCheckFlag("0");
            couponInfo.setCheckComments("补贴已失效");
        }
        return couponInfo;
    }

    @RequestMapping("/update")
    public ResponseBo update(CouponInfo couponInfo) {
        couponInfo = getCheckInfo(couponInfo);
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
        CouponInfo couponInfo = couponService.getByCouponId(couponId);
        return ResponseBo.okWithData(null, couponInfo);
    }

    /**
     * 获取短链
     * @return
     */
    @RequestMapping("/getShortUrl")
    public ResponseBo getShortUrl(String url) {
        String shortUrl;
        if (StringUtils.isNotEmpty(url)) {
            shortUrl = shortUrlService.genConponShortUrl(url);
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
    public ResponseBo deleteCoupon(@RequestParam("couponId") String couponId) {
        String msg = "";
        if(StringUtils.isNotEmpty(couponId)) {
            List<String> ids = Arrays.asList(couponId.split(","));
            List<String> couponIds = couponService.isCouponUsed(ids);
            List<String> others = ids.stream().filter(x->!couponIds.contains(x)).collect(Collectors.toList());
            if(others.size() != 0) {
                couponService.deleteCoupon(others);
            }
            msg = "删除成功，共删除" + others.size() + "条记录";
            if(couponIds.size()>0) {
                msg += "，"+couponIds.size()+"条记录被引用，无法删除。";
            }else {
                msg += "。";
            }
        }else {
            return ResponseBo.error();
        }
        return ResponseBo.ok(msg);
    }

    @RequestMapping("/deleteCouponGroup")
    public ResponseBo deleteCouponGroup(@RequestParam("groupId") String groupId) {
        couponService.deleteCouponGroup(groupId);
        return ResponseBo.ok();
    }

    /**
     * 获取券引用名的长度限制
     * @return
     */
    @RequestMapping("/validCouponNameLen")
    public boolean validCouponNameLen(@RequestParam("couponName") String couponName) {
        return dailyProperties.getCouponNameLen() >= couponName.length();
    }

    /**
     * 同步系统计算的券
     * @return
     */
    @GetMapping("/getCalculatedCoupon")
    public ResponseBo getCalculatedCoupon() {
        couponService.getCalculatedCoupon();
        return ResponseBo.ok();
    }
}