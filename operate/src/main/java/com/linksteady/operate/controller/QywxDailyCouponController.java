package com.linksteady.operate.controller;

import com.alibaba.fastjson.JSONObject;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.CouponInfo;
import com.linksteady.operate.service.CouponService;
import com.linksteady.operate.service.QywxDailyCouponService;
import com.linksteady.operate.service.impl.ShortUrlServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * 短信相关的controller
 *
 * @author huang
 */
@RestController
@RequestMapping("/qywxDailyCoupon")
@Slf4j
public class QywxDailyCouponController extends BaseController {

    @Autowired
    QywxDailyCouponService couponService;

    /**
     * 获取所有的有效优惠券列表(不分页)
     *
     * @param
     * @return
     */
    @RequestMapping("/selectAllCouponList")
    public ResponseBo selectAllCouponList() {
        couponService.validCoupon();
        List<CouponInfo> result = couponService.selectAllCouponList();
        return ResponseBo.okWithData("", result);
    }

    /**
     * 获取券list
     * @param
     * @return
     */
    @RequestMapping("/couponList")
    public ResponseBo couponList(Integer limit, Integer offset) {
        couponService.validCoupon();
        List<CouponInfo> result = couponService.getList(limit, offset);
        int totalCount = couponService.getTotalCount();
        return ResponseBo.okOverPaging("", totalCount, result);
    }

    @RequestMapping("/save")
    public synchronized ResponseBo save(CouponInfo couponInfo){
        //couponInfo.setCouponSource("1");
      //  couponInfo.setCouponSn(1);
        String couponIdentity = couponInfo.getCouponIdentity();//优惠券编号，必填不能重复
        //判断优惠券编号是否重复
        boolean flag = couponService.selectCouponIdentity(couponIdentity);
        if(flag){
            return ResponseBo.error("优惠券编号重复！");
        }
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
        //对券进行校验
        couponInfo = getCheckInfo(couponInfo);
        //保存
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
     * 删除组-券关系
     * @param
     * @return
     */
    @RequestMapping("/deleteCoupon")
    public ResponseBo deleteCoupon(@RequestParam("couponId") String couponId) {
        if(StringUtils.isNotEmpty(couponId)) {
            List<String> ids = Arrays.asList(couponId.split(","));
            try {
                couponService.deleteCoupon(ids);
                return ResponseBo.ok("删除成功!");
            } catch (Exception e) {
                log.error("删除优惠券失败，失败原因为{}",e);
                return ResponseBo.error("删除失败!");
            }
        }else
        {
            return ResponseBo.error("删除失败，没有找到选择的优惠券!");
        }

    }

    /**
     * 智能补贴弹出面板-保存
     * @return
     */
    @GetMapping("/getCalculatedCoupon")
    public ResponseBo getCalculatedCoupon(@RequestParam String coupon) {
        List<CouponInfo> dataList = JSONObject.parseArray(coupon, CouponInfo.class);
        couponService.getCalculatedCoupon(dataList);
        return ResponseBo.ok();
    }

    /**
     * 验证券名称是否相同
     * @return
     */
    @RequestMapping("/checkCouponName")
    public boolean checkCouponName(@RequestParam("couponDisplayName") String couponDisplayName, @RequestParam("operate") String operate) {
        if("update".equalsIgnoreCase(operate)) {
            return true;
        }else {
            return couponService.checkCouponName(couponDisplayName) == 0;
        }
    }

    /**
     * 验证券信息
     * @return
     */
    @RequestMapping("/validCoupon")
    public ResponseBo validCoupon() {
        couponService.validCoupon();
        return ResponseBo.ok();
    }

    /**
     * 获取智能券
     * @return
     */
    @GetMapping("/getIntelCouponList")
    public List<CouponInfo> getIntelCouponList() {
        return couponService.getIntelCouponList();
    }

}