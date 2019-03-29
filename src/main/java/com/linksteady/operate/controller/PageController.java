package com.linksteady.operate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/page")
public class PageController {

    /**
     * 核心指标概况
     */
    @RequestMapping("/coreindicators")
    public String coreIndicators_month() {
        return "operate/coreIndicators/month";
    }

    @RequestMapping("/coreindicators/year")
    public String coreIndicators_year() {
        return "operate/coreIndicators/year";
    }
}
