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
 * @author
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

    /**
     * 获取文案列表
     * @param headId
     * @return
     */
    @GetMapping("/getSmsTemplateList")
    public ResponseBo getSmsTemplateList(Long headId,String isPersonal,String scene) {
        return ResponseBo.okWithData(null, activityTemplateService.getSmsTemplateList(headId,isPersonal,scene));
    }

    /**
     * 根据文案编码获取文案
     * @param code
     * @return
     */
    @GetMapping("/getTemplate")
    public ResponseBo getTemplate(@RequestParam("code") Long code) {
        return ResponseBo.okWithData(null, activityTemplateService.getTemplate(code));
    }

    @PostMapping("/updateSmsTemplate")
    public ResponseBo updateSmsTemplate(ActivityTemplate activityTemplate,String flag) {
        String result=activityTemplateService.updateSmsTemplate(activityTemplate,flag);
        return ResponseBo.ok(result);
    }

    /**
     *
     * @param code  当前要删除的活动文案的编码
     * @param headId 活动头ID
     * @return stage 活动所处阶段
     */
    @PostMapping("/deleteActivityTemplate")
    public ResponseBo deleteActivityTemplate(@RequestParam("code") Long code,Long headId,String stage,String type) {
        //如果文案被当前活动的当前阶段引用的话，可以直接删除，否则给出提示
        if(activityTemplateService.checkTemplateUsed(code,headId,stage,type))
        {
            return ResponseBo.error("当前文案已在其它活动被引用，无法删除！");
        }else
        {
            activityTemplateService.deleteActivityTemplate(code,headId,stage,type);
            return ResponseBo.ok();
        }

    }

    /**
     * 屏蔽测试-短信模板
     * @return
     */
    @GetMapping("/getActivityTemplateContent")
    public ResponseBo getActivityTemplateContent(@RequestParam("code") Long code) {
        return ResponseBo.okWithData(null, activityTemplateService.getActivityTemplateContent(code,"DISPLAY"));
    }

    /**
     * 活动运营短信文案发送测试
     */
    @RequestMapping("/activityContentTestSend")
    public ResponseBo testSend(String phoneNum, Long smsCode) {
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
     * @param groupId
     * @return
     */
    @PostMapping("/removeSmsSelected")
    public ResponseBo removeSmsSelected(@RequestParam String type,@RequestParam Long headId, @RequestParam String stage, @RequestParam Long groupId) {
        activityTemplateService.removeSmsSelected(type, headId, stage, groupId);
        return ResponseBo.ok();
    }

    /**
     * 为群组设置文案
     * @return
     */
    @PostMapping("/setSmsCode")
    public ResponseBo setSmsCode(@RequestParam("groupId") Long groupId, @RequestParam("tmpCode") Long tmpCode,
                                 @RequestParam("headId") Long headId, @RequestParam("type") String type, @RequestParam("stage") String stage) {
        activityTemplateService.setSmsCode(groupId, tmpCode, headId, type, stage);
        return ResponseBo.ok();
    }

    /**
     * 判断文案是否已在其它地方被引用
     * @return
     */
    @PostMapping("/checkTemplateUsed")
    public ResponseBo checkTemplateUsed(@RequestParam("code") Long code,Long headId,String stage,String type) {
        boolean flag=activityTemplateService.checkTemplateUsed(code, headId, type, stage);
        return ResponseBo.ok(flag==true?"Y":"N");
    }
}
