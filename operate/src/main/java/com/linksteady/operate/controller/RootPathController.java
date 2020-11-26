package com.linksteady.operate.controller;

import com.linksteady.common.bo.UserBo;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.service.CommonFunService;
import com.linksteady.operate.config.PushConfig;
import com.linksteady.smp.starter.lognotice.service.ExceptionNoticeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author hxcao
 * @date 2019-07-19
 */
@Slf4j
@Controller
@RequestMapping("/")
public class RootPathController extends BaseController {

    @Autowired
    CommonFunService commonFunService;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @Autowired
    private PushConfig pushConfig;

    @RequestMapping("/")
    public String root() {
        return "redirect:index";
    }

    @RequestMapping("/index")
    public String index(Model model) {
        // 登录成后，即可通过 Subject 获取登录的用户信息
        UserBo userBo = super.getCurrentUser();
        model.addAttribute("user", userBo);
        return "index";
    }

    /**
     * 推送控制页
     * @return
     */
    @RequestMapping("/push")
    public String push(Model model) {
        if(pushConfig != null) {
            String status = pushConfig.getPushFlag();
            if(StringUtils.isNotEmpty(status)) {
                model.addAttribute("status", status);
            }
        }
        return "operate/push/dashbord";
    }

    /**
     * 桑基图
     * @return
     */
    @RequestMapping("/sankey")
    public String sankeyChart(@RequestParam("dateRange") String dateRange, Model model) {
        model.addAttribute("dateRange", dateRange);
        return "operate/insight/sankey";
    }

    @RequestMapping("/fullScreen")
    public String fullScreen() {
        return "operate/insight/fullscreen";
    }
}