package com.linksteady.operate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


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
}
