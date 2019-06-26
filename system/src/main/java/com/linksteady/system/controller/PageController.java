package com.linksteady.system.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/")
public class PageController extends BaseController {

    @Value("${app.version}")
    private String version;

    @RequestMapping("/index")
        public String index(Model model) {
        // 登录成后，即可通过 Subject 获取登录的用户信息
        User user = super.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("version", version);
        return "index";
    }

    @RequestMapping("/main")
    public String main(Model model) {
        // 登录成后，即可通过 Subject 获取登录的用户信息
        User user = super.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("version", version);
        return "main";
    }

    @RequestMapping("/getSysIdFromSession")
    @ResponseBody
    public ResponseBo getSysIdFromSession(HttpServletRequest request) {
        String sysId = String.valueOf(request.getSession().getAttribute("sysId"));
        return ResponseBo.okWithData(null, sysId);
    }

    @RequestMapping("/setSysIdToSession")
    @ResponseBody
    public ResponseBo setSysIdToSession(HttpServletRequest request, @RequestParam("sysId") String sysId) {
        request.getSession().setAttribute("sysId", sysId);
        return ResponseBo.ok();
    }

    @RequestMapping("/resetPass")
    public String resetPass(Model model) {
        // 登录成后，即可通过 Subject 获取登录的用户信息
        User user = super.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("version", version);

        if(null!=user.getFirstLogin()&&"Y".equals(user.getFirstLogin()))
        {
            return "resetPassword";
        }else
        {
            return "main";
        }

    }
}
