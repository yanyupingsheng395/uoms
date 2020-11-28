package com.linksteady.qywx.controller;

import com.linksteady.common.bo.UserBo;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.domain.ExternalContact;
import com.linksteady.qywx.service.ExternalContactService;
import com.linksteady.qywx.service.QywxService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
@Slf4j
@Controller
@RequestMapping("/qwClient")
public class QywxClientController {

    @Autowired
    QywxService qywxService;
    @Autowired
    private ExternalContactService externalContactService;

    private static final String SALT="linksteady";

    /**
     * 请求首页
     *
     * @return
     */
    @RequestMapping("/index")
    public String index(Model model, HttpServletRequest request) {
        UserBo userBo =(UserBo) SecurityUtils.getSubject().getPrincipal();
        String isAdmin = "N";
        if (userBo != null) {
            isAdmin = StringUtils.isNotEmpty(isAdmin) ? isAdmin : "N";
        }
        model.addAttribute("isAdmin", isAdmin);
        return "qywxClient/qywxindex";
    }
    /**
     * 请求首页
     *
     * @return
     */
    @RequestMapping("/main")
    public String main() {
        return "qywxClient/main";
    }
    /**
     * 导购运营引导
     *
     * @return
     */
    @RequestMapping("/guidance")
    public String guidance() {
        return "qywxClient/guidance/list";
    }

    /**
     * 导购关系引导(根据给定的条件获取对应的用户)
     */
    @RequestMapping("/getGuidanceList")
    @ResponseBody
    public ResponseBo getGuidanceList(@RequestParam("relation") String relation,
                                      @RequestParam("loss") String loss,
                                      @RequestParam("stagevalue") String stagevalue,
                                      @RequestParam("interval") String interval,
                                      Integer limit,Integer offset)
    {
        UserBo userBo =(UserBo) SecurityUtils.getSubject().getPrincipal();
        String followUserId=userBo.getUsername();
        int count=externalContactService.getQywxGuidanceCount(followUserId, relation, loss, stagevalue, interval);
        List<ExternalContact> list = externalContactService.getQywxGuidanceList(followUserId, relation, loss, stagevalue, interval,offset,limit);
        return ResponseBo.okOverPaging(null, count, list);
    }

    /**
     *导购关系引导中未购买客户
     * 按添加时间条件查询
     */
    @RequestMapping("/getAddTimeList")
    @ResponseBody
    public ResponseBo getAddTimeList(@RequestParam("addtime") String addtime,Integer limit,Integer offset){
        UserBo userBo =(UserBo) SecurityUtils.getSubject().getPrincipal();
        String followUserId =userBo.getUsername();
        int count=externalContactService.getgetAddTimeCount(followUserId);
        List<ExternalContact> list = externalContactService.getAddTimeList(followUserId, addtime,offset,limit);
        return ResponseBo.okOverPaging(null, count, list);
    }
}
