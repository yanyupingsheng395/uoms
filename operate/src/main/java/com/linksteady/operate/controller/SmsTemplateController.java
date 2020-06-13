package com.linksteady.operate.controller;

import com.google.common.base.Splitter;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.PushProperties;
import com.linksteady.operate.domain.SmsTemplate;
import com.linksteady.operate.service.SmsTemplateService;
import com.linksteady.operate.service.impl.RedisMessageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    private PushProperties pushProperties;

    @Autowired
    RedisMessageServiceImpl redisMessageService;


    /**
     * 获取短信模板列表 (增加排序，将当前组引用的文案排在第一个)  每日运营配置处使用
     *
     * @param
     * @return
     */
    @RequestMapping("/selectSmsTemplateListWithGroup")
    public ResponseBo selectSmsTemplateListWithGroup(Integer limit, Integer offset,Long groupId) {
        List<SmsTemplate> result = smsTemplateService.selectSmsTemplateListWithGroup(limit, offset,groupId);
        int totalCount = smsTemplateService.getTotalCount();
        return ResponseBo.okOverPaging("", totalCount, result);
    }

    /**
     * 获取短信模板列表
     *
     * @param
     * @return
     */
    @RequestMapping("/smsTemplateList")
    public ResponseBo smsTemplateList(Integer limit, Integer offset) {
        List<SmsTemplate> result = smsTemplateService.selectSmsTemplateList(limit, offset);
        int totalCount = smsTemplateService.getTotalCount();
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
    public ResponseBo deleteSmsTemplate(@RequestParam("smsCode") String smsCode) {
        //删除文案
        smsTemplateService.deleteSmsTemplate(smsCode);
        return ResponseBo.ok();
    }

    /**
     * 查询文案是否被引用
     * @param smsCode
     * @return
     */
    @RequestMapping("/smsIsUsed")
    public boolean smsIsUsed(@RequestParam("smsCode") String smsCode) {
        return smsTemplateService.refrenceCount(smsCode) > 0;
    }

    /**
     * 获取短信模板
     */
    @RequestMapping("/getSmsTemplate")
    public ResponseBo getSmsTemplate(HttpServletRequest request) {
        String smsCode = request.getParameter("smsCode");
        return ResponseBo.okWithData("", smsTemplateService.getSmsTemplateBySmsCode(smsCode));
    }

    /**
     * 读取文案(变量已被样例值替换)
     * @param request
     * @return
     */
    @RequestMapping("/getSmsTemplateContent")
    public ResponseBo getSmsTemplateContent(HttpServletRequest request) {
        String smsCode = request.getParameter("smsCode");
        return ResponseBo.okWithData("", smsTemplateService.getSmsContent(smsCode,"DISPLAY"));
    }

    /**
     * 发送测试
     */
    @RequestMapping("/testSend")
    public ResponseBo testSend(String phoneNum, String smsCode) {
        List<String> phoneNumList=Splitter.on(",").trimResults().omitEmptyStrings().splitToList(phoneNum);

        String smsContent=smsTemplateService.getSmsContent(smsCode,"SEND");
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
        return ResponseBo.okWithData(null, pushProperties.getCouponSendType());
    }

    @GetMapping("/validSmsContentLength")
    public ResponseBo validSmsContentLength(@RequestParam("smsContent") String smsContent) {
        return ResponseBo.okWithData(null, smsContent.length() <= pushProperties.getSmsLengthLimit());
    }

    /**
     * 当前文案被引用的群组信息
     * @param smsCode
     * @return
     */
    @RequestMapping("/getSmsUsedGroupInfo")
    public ResponseBo getSmsUsedGroupInfo(@RequestParam("smsCode") String smsCode) {
        return ResponseBo.okWithData(null,smsTemplateService.getSmsUsedGroupInfo(smsCode));
    }
}

