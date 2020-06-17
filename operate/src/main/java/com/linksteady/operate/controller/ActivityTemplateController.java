package com.linksteady.operate.controller;

import com.google.common.base.Splitter;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.ActivityTemplate;
import com.linksteady.operate.service.ActivityTemplateService;
import com.linksteady.operate.service.impl.RedisMessageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 活动运营文案
 */
@Slf4j
@RestController
@RequestMapping("/activity")
public class ActivityTemplateController {

    @Autowired
    private ActivityTemplateService activityTemplateService;

    @Autowired
    RedisMessageServiceImpl redisMessageService;

    /**
     * 保存文案信息
     */
    @PostMapping("/saveSmsTemplate")
    public ResponseBo saveSmsTemplate(ActivityTemplate activityTemplate) {
        activityTemplateService.saveTemplate(activityTemplate);
        return ResponseBo.ok();
    }

    @GetMapping("/getSmsTemplateList")
    public ResponseBo getSmsTemplateList(ActivityTemplate activityTemplate) {
        return ResponseBo.okWithData(null, activityTemplateService.getSmsTemplateList(activityTemplate));
    }

    @GetMapping("/getTemplate")
    public ResponseBo getTemplate(@RequestParam("code") String code) {
        return ResponseBo.okWithData(null, activityTemplateService.getTemplate(code));
    }

    @PostMapping("/updateSmsTemplate")
    public ResponseBo updateSmsTemplate(ActivityTemplate activityTemplate) {
        activityTemplateService.update(activityTemplate);
        return ResponseBo.ok();
    }

    @PostMapping("/deleteTmp")
    public ResponseBo deleteTmp(@RequestParam("code") String code) {
        activityTemplateService.deleteTemplate(code);
        return ResponseBo.ok();
    }

    /**
     * 屏蔽测试-短信模板
     * @return
     */
    @GetMapping("/getActivityTemplateContent")
    public ResponseBo getActivityTemplateContent(@RequestParam("code") String code) {
        return ResponseBo.okWithData(null, activityTemplateService.getActivityTemplateContent(code,"DISPLAY"));
    }

    /**
     * 活动运营短信文案发送测试
     */
    @RequestMapping("/activityContentTestSend")
    public ResponseBo testSend(String phoneNum, String smsCode) {
        List<String> phoneNumList= Splitter.on(",").trimResults().omitEmptyStrings().splitToList(phoneNum);

        String smsContent=activityTemplateService.getActivityTemplateContent(smsCode,"SEND");
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
     * 移除群组的文案
     * @param type 活动类型
     * @param headId
     * @param stage
     * @param smsCode
     * @param groupId
     * @return
     */
    @PostMapping("/removeSmsSelected")
    public ResponseBo removeSmsSelected(@RequestParam String type,@RequestParam String headId, @RequestParam String stage, @RequestParam String smsCode, @RequestParam String groupId) {
        activityTemplateService.removeSmsSelected(type, headId, stage, smsCode, groupId);
        return ResponseBo.ok();
    }

    /**
     * 校验当前短信模板是否被引用
     * @return
     */
    @GetMapping("/checkTmpIsUsed")
    public ResponseBo checkTmpIsUsed(@RequestParam("tmpCode") String tmpCode) {
        return ResponseBo.okWithData(null, activityTemplateService.checkTmpIsUsed(tmpCode));
    }

    /**
     * 为群组设置文案
     * @return
     */
    @PostMapping("/setSmsCode")
    public ResponseBo setSmsCode(@RequestParam("groupId") String groupId, @RequestParam("tmpCode") String tmpCode,
                                 @RequestParam("headId") Long headId, @RequestParam("type") String type, @RequestParam("stage") String stage) {
        activityTemplateService.setSmsCode(groupId, tmpCode, headId, type, stage);
        return ResponseBo.ok();
    }
}
