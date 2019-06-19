package com.linksteady.system.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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

}
