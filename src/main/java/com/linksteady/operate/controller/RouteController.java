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
    public String index() {
        return "operate/reason/reasonlist";
    }
}
