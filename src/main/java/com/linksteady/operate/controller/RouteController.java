package com.linksteady.operate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 负责提供导航路由的controller
 */
@Controller
@RequestMapping("/")
public class RouteController {

    @RequestMapping("/reason/gotoIndex")
    public String reasonIndex() {
        return "operate/reason/reasonlist";
    }

    @RequestMapping("/reason/add")
    public String reasonAdd() {
        return "operate/reason/reasonadd";
    }

    @RequestMapping("/reason/viewReason")
    public String view(@RequestParam  String reasonId, Model model) {
        model.addAttribute("reasonId", reasonId);
        return "operate/reason/reasonDetail";
    }
}
