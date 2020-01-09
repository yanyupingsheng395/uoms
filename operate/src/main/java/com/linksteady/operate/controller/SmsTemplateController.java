package com.linksteady.operate.controller;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.SmsTemplate;
import com.linksteady.operate.service.SmsTemplateService;
import com.linksteady.operate.service.impl.RedisMessageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 短信相关的controller
 *
 * @author huang
 */
@RestController
@RequestMapping("/smsTemplate")
@Slf4j
public class SmsTemplateController extends BaseController {

    @Autowired
    SmsTemplateService smsTemplateService;

    @Autowired
    private DailyProperties dailyProperties;

    @Autowired
    RedisMessageServiceImpl redisMessageService;

    /**
     * 获取短信模板
     *
     * @param
     * @return
     */
    @RequestMapping("/list")
    public ResponseBo smsTemplateList(@RequestBody QueryRequest request) {
        SmsTemplate smsTemplate = new SmsTemplate();
        smsTemplate.setUserValue(request.getParam().get("userValue"));
        smsTemplate.setLifeCycle(request.getParam().get("lifeCycle"));
        smsTemplate.setPathActive(request.getParam().get("pathActive"));
        List<SmsTemplate> result = smsTemplateService.getSmsTemplateList((request.getPageNum() - 1) * request.getPageSize() + 1, request.getPageNum() * request.getPageSize(), smsTemplate);
        int totalCount = smsTemplateService.getTotalCount(smsTemplate);
        return ResponseBo.okOverPaging("", totalCount, result);
    }

    /**
     * 增加短信模板
     */
    @RequestMapping("/addSmsTemplate")
    public ResponseBo addSmsTemplate(SmsTemplate smsTemplate) {
        //添加
        try {
            smsTemplateService.saveSmsTemplate(smsTemplate);
            return ResponseBo.ok();
        } catch (Exception e) {
            //失败
            return ResponseBo.error();
        }
    }

    /**
     * 删除短信模板
     */
    @RequestMapping("/deleteSmsTemplate")
    public ResponseBo deleteSmsTemplate(HttpServletRequest request) {
        String smsCode = request.getParameter("smsCode");
        //判断短信模板是否被引用
        if (smsTemplateService.refrenceCount(smsCode) > 0) {
            return ResponseBo.error("此文案已经被成长组引用，无法删除！");
        }
        smsTemplateService.deleteSmsTemplate(smsCode);
        return ResponseBo.ok();
    }

    /**
     * 获取短信模板
     */
    @RequestMapping("/getSmsTemplate")
    public ResponseBo getSmsTemplate(HttpServletRequest request) {
        String smsCode = request.getParameter("smsCode");
        //判断短信模板是否被引用
        if (smsTemplateService.refrenceCount(smsCode) > 0) {
            return ResponseBo.error("此文案已经被成长组引用，无法编辑！");
        }
        return ResponseBo.okWithData("", smsTemplateService.getSmsTemplate(smsCode));
    }

    /**
     * 发送测试
     */
    @RequestMapping("/testSend")
    public ResponseBo testSend(String phoneNum, String smsContent) {

        List<String> phoneNumList=Splitter.on(",").trimResults().omitEmptyStrings().splitToList(phoneNum);

        try {
            for(String num:phoneNumList)
            {
                redisMessageService.sendPhoneMessage(num,smsContent);
            }
            return ResponseBo.ok("测试发送成功！");
        } catch (Exception e) {
            log.info("发送测试短信错误，错误原因:{}",e);
            return ResponseBo.error("测试发送失败！");
        }
    }

    /**
     * 修改短信模板
     *
     * @param smsTemplate
     * @return
     */
    @RequestMapping("/updateSmsTemplate")
    public ResponseBo updateSmsTemplate(SmsTemplate smsTemplate) {
        smsTemplateService.update(smsTemplate);
        return ResponseBo.ok();
    }

    /**
     * 获取优惠券发送方式
     *
     * @return
     */
    @RequestMapping("/getCouponSendType")
    public ResponseBo getCouponSendType() {
        return ResponseBo.okWithData(null, dailyProperties.getCouponSendType());
    }

    /**
     * 计算字数
     *0
     * @return
     */
    @RequestMapping("/calFontNum")
    public ResponseBo calFontNum(String smsContent) {
        Map<String, Object> result = Maps.newHashMap();
        String couponUrl = "${COUPON_URL}";
        String couponName = "${COUPON_NAME}";
        String prodName = "${PROD_NAME}";
        String prodUrl = "${PROD_URL}";
        int templateCount = smsContent.length();
        if (smsContent.contains(couponUrl)) {
            templateCount = templateCount - couponUrl.length() + dailyProperties.getShortUrlLen();
        }
        if (smsContent.contains(couponName)) {
            templateCount = templateCount - couponName.length() + dailyProperties.getCouponNameLen();
        }
        if (smsContent.contains(prodName)) {
            templateCount = templateCount - prodName.length() + dailyProperties.getProdNameLen();
        }
        if (smsContent.contains(prodUrl)) {
            templateCount = templateCount - prodUrl.length() + dailyProperties.getShortUrlLen();
        }
        result.put("count", templateCount);
        result.put("valid", templateCount <= dailyProperties.getSmsLengthLimit());
        return ResponseBo.okWithData(null, result);
    }

    @GetMapping("/validSmsContentLength")
    public ResponseBo validSmsContentLength(@RequestParam("smsContent") String smsContent) {
        return ResponseBo.okWithData(null, smsContent.length() <= dailyProperties.getSmsLengthLimit());
    }

    @RequestMapping("/getTemplateByGroupId")
    public ResponseBo getTemplateByGroupId(@RequestParam("groupId") String groupId) {
        return ResponseBo.okWithData(null, smsTemplateService.getTemplateByGroupId(groupId));
    }
}

