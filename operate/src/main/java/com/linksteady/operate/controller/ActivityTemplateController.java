package com.linksteady.operate.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.ActivityTemplate;
import com.linksteady.operate.domain.SmsTemplate;
import com.linksteady.operate.push.impl.PushMessageServiceImpl;
import com.linksteady.operate.service.ActivityTemplateService;
import com.linksteady.operate.service.ActivityUserGroupService;
import com.linksteady.operate.service.SmsTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 短信相关的controller
 * @author huang
 */
@RestController
@RequestMapping("/activityTemplate")
@Slf4j
public class ActivityTemplateController extends BaseController {

    @Autowired
    private ActivityTemplateService activityTemplateService;

    @Autowired
    PushMessageServiceImpl pushMessageService;

    @Autowired
    private ActivityUserGroupService activityUserGroupService;

    /**
     * 获取短信模板
     * @param
     * @return
     */
    @RequestMapping("/list")
    public ResponseBo templateList(@RequestBody QueryRequest request) {
        String code = request.getParam().get("code");
        int start =request.getStart();
        int end = request.getEnd();
        List<ActivityTemplate> result=activityTemplateService.getTemplateList(start, end,code);
        int totalCount= activityTemplateService.getTotalCount(code);
        return  ResponseBo.okOverPaging("",totalCount,result);
    }


    /**
     * 增加短信模板
     */
    @RequestMapping("/addTemplate")
    public ResponseBo addTemplate(@RequestBody ActivityTemplate activityTemplate) {
           //添加
        try {
            activityTemplateService.saveTemplate(activityTemplate);
            return ResponseBo.ok();
        } catch (Exception e) {
            //失败
            return ResponseBo.error();
        }
    }

    /**
     * 删除短信模板
     */
    @RequestMapping("/deleteTemplate")
    public ResponseBo deleteTemplate(HttpServletRequest request) {
        String code=request.getParameter("code");
        //判断短信模板是否被引用
        if(activityUserGroupService.refrenceCount(code)>0)
        {
            return ResponseBo.error("此模板已经被活动群组引用，无法删除！");
        }
        activityTemplateService.deleteTemplate(code);
        return ResponseBo.ok();
    }

    /**
     * 获取短信模板
     */
    @RequestMapping("/getTemplate")
    public ResponseBo getTemplate(HttpServletRequest request) {
        String code=request.getParameter("code");
        return ResponseBo.okWithData("",activityTemplateService.getTemplate(code));
    }

    /**
     * 修改短信模板
     * @param activityTemplate
     * @return
     */
    @RequestMapping("/updateTemplate")
    public ResponseBo updateTemplate(@RequestBody ActivityTemplate activityTemplate) {
        activityTemplateService.update(activityTemplate);
        return ResponseBo.ok();
    }

    @RequestMapping("/checkCode")
    public ResponseBo checkCode(@RequestParam String code) {
        return ResponseBo.okWithData(null, activityTemplateService.checkCode(code));
    }
}

