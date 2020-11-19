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
}
