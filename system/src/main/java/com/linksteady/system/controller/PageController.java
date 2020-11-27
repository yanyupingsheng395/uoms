package com.linksteady.system.controller;

import com.linksteady.common.annotation.Log;
import com.linksteady.common.bo.UserBo;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.service.CommonFunService;
import com.linksteady.smp.starter.lognotice.service.ExceptionNoticeHandler;
import com.linksteady.common.domain.User;
import com.linksteady.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author huang
 *
 */
@Slf4j
@Controller
@RequestMapping("/")
public class PageController extends BaseController {

    @Autowired
    CommonFunService commonFunService;

    @Autowired
    private UserService userService;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @RequestMapping("/index")
        public String index(Model model) {
        // 登录成后，即可通过 Subject 获取登录的用户信息
        UserBo userBo = super.getCurrentUser();
        model.addAttribute("user", userBo);
        return "index";
    }

    @Log("重置密码")
    @RequestMapping("/resetPass")
    public String resetPass(Model model) {
        // 登录成后，即可通过 Subject 获取登录的用户信息
        UserBo userBo = super.getCurrentUser();
        User user = this.userService.findUserProfile(userBo.getUserId());
        model.addAttribute("user", user);

        if(null!=user.getFirstLogin()&&"Y".equals(user.getFirstLogin()))
        {
            return "resetPassword";
        }else
        {
            return "index";
        }
    }

    /**
     * 系统消息
     *
     * @return
     */
    @RequestMapping("/msg")
    @Log(value = "系统消息", location = "系统管理")
    public String msgPage() {
        return "msg/list";
    }
}
