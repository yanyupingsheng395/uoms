package com.linksteady.qywx.controller;

import com.linksteady.common.annotation.Log;
import com.linksteady.common.controller.BaseController;
import com.linksteady.qywx.config.PushConfig;
import com.linksteady.qywx.domain.AddUserHead;
import com.linksteady.qywx.domain.QywxParam;
import com.linksteady.qywx.domain.QywxWelcome;
import com.linksteady.qywx.service.AddUserService;
import com.linksteady.qywx.service.AddUserTriggerService;
import com.linksteady.qywx.service.QywxParamService;
import com.linksteady.qywx.service.WelcomeService;
import com.linksteady.qywx.vo.SourceConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/page")
public class PageController  extends BaseController {

    @Autowired
    WelcomeService welcomeService;

    @Autowired
    PushConfig pushConfig;

    @Autowired
    private AddUserService addUserService;

    @Autowired
    private QywxParamService qywxParamService;


    @Autowired
    private AddUserTriggerService addUserTriggerService;

    /**
     * 活动运营
     *
     * @return
     */
    @RequestMapping("/activity")
    public String activity() {
        return "qywx/activity/list";
    }

    /**
     * 企业微信应用配置
     */
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

    /**
     * 拉新效果监控
     */
    @RequestMapping("/qywxAddUserMonitor")
    public String addUserMonitor() {
        return "qywx/addUserMonitor/monitor";
    }

    /**
     * 设置欢迎语
     */
    @RequestMapping("/qywxWelcome")
    public String welcome() {
        return "qywx/welcome/list";
    }

    /**
     * 添加欢迎语
     */
    @RequestMapping("/qywxWelcome/add")
    public String addWelcome() {
        return "qywx/welcome/add";
    }

    /**
     *更新欢迎语
     */
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

    /**
     * 添加外部联系人-列表
     */
    @RequestMapping("/addUser")
    public String addUserList() {
        return "qywx/addUser/list";
    }

    /**
     * 添加外部联系人-新增
     */
    @RequestMapping("/addUser/add")
    public String addUserAdd(Model model) {
        QywxParam qywxParam = qywxParamService.getQywxParam();
        model.addAttribute("qywxParam", qywxParam);
        SourceConfigVO sourceConfigVO = SourceConfigVO.getInstance(pushConfig);
        model.addAttribute("sourceConfig", sourceConfigVO);
        model.addAttribute("opType", "save");
        return "qywx/addUser/add";
    }

    /**
     * 添加外部联系人-效果
     */
    @RequestMapping("/addUser/effect")
    public String addUserEffect(String id, Model model) {
        AddUserHead addUserHead = addUserService.getHeadById(Long.parseLong(id));

        if (null == addUserHead || "edit".equals(addUserHead.getTaskStatus())) {
            model.addAttribute("msg", "计划中的任务不支持查看效果！");
            return "qywx/addUser/list";
        } else {
            //判断是否存在已经执行的记录
            int count = addUserService.getScheduleCount(Long.parseLong(id));
            if (count == 0) {
                model.addAttribute("msg", "任务至少被执行一次后才能查看效果！");
                return "qywx/addUser/list";
            } else {
                Map<String, Object> data = addUserService.getTaskResultData(id);
                model.addAttribute("id", id);
                model.addAttribute("data", data);
                return "qywx/addUser/effect";
            }
        }
    }

    /**
     * 添加外部联系人-编辑
     */
    @RequestMapping("/addUser/edit")
    public String addUserEdit(@RequestParam String id, Model model) {
        model.addAttribute("opType", "update");
        AddUserHead addUserHead = addUserService.getHeadById(Long.parseLong(id));

        if (null == addUserHead || "abort".equals(addUserHead.getTaskStatus())) {
            model.addAttribute("msg", "已终止的任务不允许被编辑！");
            return "qywx/addUser/list";
        } else {
            SourceConfigVO sourceConfigVO = SourceConfigVO.getInstance(pushConfig);
            model.addAttribute("sourceConfig", sourceConfigVO);
            model.addAttribute("addUserHead", addUserHead);
            return "qywx/addUser/add";
        }
    }

    /**
     * 添加外部联系人-列表
     */
    @RequestMapping("/addUserTrigger")
    public String addUserTriggerList() {
        return "qywx/addUserTrigger/list";
    }

    /**
     * 添加外部联系人-新增
     */
    @RequestMapping("/addUserTrigger/add")
    public String addUserTriggerAdd(Model model) {
        QywxParam qywxParam = qywxParamService.getQywxParam();
        model.addAttribute("qywxParam", qywxParam);
        SourceConfigVO sourceConfigVO = SourceConfigVO.getInstance(pushConfig);
        model.addAttribute("sourceConfig", sourceConfigVO);
        model.addAttribute("opType", "save");
        return "qywx/addUserTrigger/add";
    }

    /**
     * 添加外部联系人-效果
     */
    @RequestMapping("/addUserTrigger/effect")
    public String addUserTriggerEffect(String id, Model model) {
        AddUserHead addUserHead = addUserTriggerService.getHeadById(Long.parseLong(id));

        if (null == addUserHead || "edit".equals(addUserHead.getTaskStatus())) {
            model.addAttribute("msg", "计划中的任务不支持查看效果！");
            return "qywx/addUserTrigger/list";
        } else {
            //判断是否存在已经执行的记录
            int count = addUserTriggerService.getScheduleCount(Long.parseLong(id));
            if (count == 0) {
                model.addAttribute("msg", "任务至少被执行一次后才能查看效果！");
                return "qywx/addUserTrigger/list";
            } else {
                Map<String, Object> data = addUserTriggerService.getTaskResultData(id);
                model.addAttribute("id", id);
                model.addAttribute("data", data);
                return "qywx/addUserTrigger/effect";
            }
        }
    }

    /**
     * 添加外部联系人-编辑
     */
    @RequestMapping("/addUserTrigger/edit")
    public String addUserTriggerEdit(@RequestParam String id, Model model) {
        QywxParam qywxParam = qywxParamService.getQywxParam();
        model.addAttribute("qywxParam", qywxParam);
        model.addAttribute("opType", "update");
        AddUserHead addUserHead = addUserTriggerService.getHeadById(Long.parseLong(id));

        if (null == addUserHead || "abort".equals(addUserHead.getTaskStatus())) {
            model.addAttribute("msg", "已终止的任务不允许被编辑！");
            return "qywx/addUserTrigger/list";
        } else {
            SourceConfigVO sourceConfigVO = SourceConfigVO.getInstance(pushConfig);
            model.addAttribute("sourceConfig", sourceConfigVO);
            model.addAttribute("addUserHead", addUserHead);
            return "qywx/addUserTrigger/add";
        }
    }
}
