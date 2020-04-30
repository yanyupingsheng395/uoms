package com.linksteady.wxofficial.controller;

import com.alibaba.fastjson.JSON;
import com.linksteady.wxofficial.common.wechat.service.OperateService;
import com.linksteady.wxofficial.config.WxProperties;
import com.linksteady.wxofficial.entity.vo.WxTagVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/4/15
 */
@Controller
@RequestMapping("/page")
public class PageController {

    @Autowired
    private OperateService operateService;

    @Autowired
    private WxProperties wxProperties;

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

    @RequestMapping("/wxOfficialUser")
    public String wxOfficialUser(Model model) {
        String url = wxProperties.getServiceDomain() + wxProperties.getTagListUrl();
        String result = operateService.getDataList(url);
        List<WxTagVo> dataList = JSON.parseArray(JSON.parseObject(result).getString("msg"), WxTagVo.class);
        model.addAttribute("tagList", dataList);
        return "wechat/wxOfficialUser/list";
    }

    @RequestMapping("/userFollow")
    public String userFollow() {
        return "wechat/response/userFollow";
    }

    @RequestMapping("/msgReply")
    public String msgReply() {
        return "wechat/response/msgReply";
    }

    @RequestMapping("/menuClick")
    public String menuClick() {
        return "wechat/response/menuClick";
    }

    @RequestMapping("/msgPush")
    public String msgPush() {
        return "wechat/msgPush/list";
    }
}
