package com.linksteady.qywx.controller;

import com.linksteady.common.annotation.Log;
import com.linksteady.common.controller.BaseController;
import com.linksteady.qywx.domain.QywxWelcome;
import com.linksteady.qywx.service.WelcomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/page")
public class PageController  extends BaseController {

    @Autowired
    WelcomeService welcomeService;

    /**
     * 活动运营
     *
     * @return
     */
    @RequestMapping("/activity")
    public String activity() {
        return "qywx/activity/list";
    }

    @RequestMapping("/application")
    public String application(){
        return "qywx/qywxsettings/applicationSetting";
    }


    /**
     * 企业微信素材(图片)上传
     *
     * @return
     */
    @RequestMapping("/qywxMediaImage")
    public String qywxMediaImage() {
        return "qywx/media/imgList";
    }


    /**
     * 企业微信部门和导购列表
     *
     * @return
     */
    @RequestMapping("/qywxBaseData/list")
    public String qywxBaseDataList() {
        return "qywx/baseData/deptUser";
    }

    /**
     * 渠道活码
     */
    @Log(value = "渠道活码", location = "用户成长系统")
    @RequestMapping("/contactWay/list")
    public String contactWayList() {
        return "qywx/contactWay/list";
    }

    @RequestMapping("/qywxAddUserMonitor")
    public String addUserMonitor() {
        return "qywx/addUserMonitor/monitor";
    }

    @RequestMapping("/qywxWelcome")
    public String welcome() {
        return "qywx/welcome/list";
    }

    @RequestMapping("/qywxWelcome/add")
    public String addWelcome() {
        return "qywx/welcome/add";
    }

    @RequestMapping("/qywxWelcome/edit")
    public String editWelcome(Model model, String id) {
        QywxWelcome qywxWelcome = welcomeService.getDataById(id);
        model.addAttribute("welcome", qywxWelcome);
        return "qywx/welcome/edit";
    }

    @RequestMapping("/qywxWelcome/effect")
    public String effectWelcome() {
        return "qywx/welcome/effect";
    }
}
