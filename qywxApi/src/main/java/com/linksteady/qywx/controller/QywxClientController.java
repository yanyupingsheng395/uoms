package com.linksteady.qywx.controller;

import com.linksteady.common.bo.UserBo;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.qywx.domain.QywxUser;
import com.linksteady.qywx.service.ExternalContactService;
import com.linksteady.qywx.service.QywxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/qwClient")
public class QywxClientController  extends BaseController {

    @Autowired
    QywxService qywxService;
    @Autowired
    private ExternalContactService externalContactService;


    /**
     * 导购运营引导
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/guidance")
    public String guidance(Model model, HttpServletRequest request) {
        return "qywx/guidance/list";
    }

    /**
     * 导购关系引导(根据给定的条件获取对应的用户)
     */
    @RequestMapping("/getGuidanceList")
    @ResponseBody
    public Map<String, Object> getGuidanceList(HttpServletRequest httpServletRequest,
                                               QueryRequest request,
                                               @RequestParam("relation") String relation,
                                               @RequestParam("loss") String loss,
                                               @RequestParam("stagevalue") String stagevalue,
                                               @RequestParam("interval") String interval)
    {
        UserBo user=(UserBo)httpServletRequest.getSession().getAttribute("user");
        String corpId=qywxService.getCorpId();
        String followUserId = user.getUsername();
        return super.selectByPageNumSize(request, () -> externalContactService.getQywxGuidanceList(corpId,followUserId,relation,loss,stagevalue,interval));
    }

    /**
     *导购关系引导中未购买客户
     * 按添加时间条件查询
     */
    @RequestMapping("/getAddTimeList")
    @ResponseBody
    public Map<String,Object> getAddTimeList(HttpServletRequest httpServletRequest,
                                             QueryRequest request,
                                             @RequestParam("addtime") String addtime){
        UserBo user=(UserBo)httpServletRequest.getSession().getAttribute("user");
        String corpId=qywxService.getCorpId();
        String followUserId = user.getUsername();
        return super.selectByPageNumSize(request, () -> externalContactService.getAddTimeList(corpId,followUserId,addtime));
    }
}
