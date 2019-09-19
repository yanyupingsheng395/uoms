package com.linksteady.operate.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.SmsTemplate;
import com.linksteady.operate.service.SmsTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 短信相关的controller
 * @author huang
 */
@RestController
@RequestMapping("/smsTemplate")
@Slf4j
public class SmsTemplateController extends BaseController {

    @Autowired
    SmsTemplateService smsTemplateService;

    /**
     * 获取短信模板
     * @param
     * @return
     */
    @RequestMapping("/list")
    public ResponseBo smsTemplateList(@RequestBody QueryRequest request) {
        String smsCode = request.getParam().get("smsCode");
        List<SmsTemplate> result=smsTemplateService.getSmsTemplateList((request.getPageNum()-1)*request.getPageSize()+1, request.getPageNum()*request.getPageSize(),smsCode);
        int totalCount= smsTemplateService.getTotalCount(smsCode);
        return  ResponseBo.okOverPaging("",totalCount,result);
    }


    /**
     * 增加短信模板
     */
    @RequestMapping("/addSmsTemplate")
    public ResponseBo getSpuFilterList(@RequestBody SmsTemplate smsTemplate) {
           //添加
        try {
            smsTemplateService.saveSmsTemplate(smsTemplate.getSmsCode(),smsTemplate.getSmsContent());
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
        String smsCode=request.getParameter("smsCode");
        //判断短信模板是否被引用
        if(smsTemplateService.refrenceCount(smsCode)>0)
        {
            return ResponseBo.error();
        }
        smsTemplateService.deleteSmsTemplate(smsCode);
        return ResponseBo.ok();
    }


//    /**
//     * 删除短信模板
//     */
//    @RequestMapping("/test")
//    public ResponseBo test(HttpServletRequest request) {
//      //  PushListThread.generatePushList("13");
//
//        return ResponseBo.ok(opDailyProperties);
//    }

}

