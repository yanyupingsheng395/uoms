package com.linksteady.qywx.controller;

import com.linksteady.common.annotation.Log;
import com.linksteady.common.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/page")
public class PageController  extends BaseController {

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
}
