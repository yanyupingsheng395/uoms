package com.linksteady.wxofficial.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author hxcao
 * @date 2020/4/15
 */
@Controller
@RequestMapping("/page")
public class PageController {

    @RequestMapping("/imageText")
    public String imageTextList() {
        return "wechat/imageText/imageTextList";
    }

    @RequestMapping("/material")
    public String materialList() {
        return "wechat/material/materialList";
    }

    @RequestMapping("/tag")
    public String tagList() {
        return "wechat/tag/list";
    }
}
