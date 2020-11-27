package com.linksteady.qywx.controller;

import com.linksteady.common.annotation.Log;
import com.linksteady.common.controller.BaseController;
import com.linksteady.qywx.config.PushConfig;
import com.linksteady.qywx.domain.*;
import com.linksteady.qywx.exception.UdfException;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.*;
import com.linksteady.qywx.vo.SourceConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Map;

@Controller
@RequestMapping("/page")
@Slf4j
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
    private QywxService qywxService;

    @Autowired
    UserService userService;


    @Autowired
    private AddUserTriggerService addUserTriggerService;

    public static final String oAuthUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?";

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

    /**
     * 系统消息
     *
     * @return
     */
    @RequestMapping("/msg")
    @Log(value = "系统消息", location = "企业微信模块")
    public String msgPage() {
        return "msg/list";
    }

    /**
     * 聊天工具栏页
     *
     * @return
     */
    @RequestMapping("/guideAssist/main")
    public String guideAssist() {
        return "qywx/guideAssist/main";
    }

    /**
     * 默认导航到介绍页
     *
     * @return
     */
    @RequestMapping("/")
    public String remark() {
        return "remark";
    }

    /**
     * 请求首页
     *
     * @return
     */
    @RequestMapping("/index")
    public String index(Model model, HttpServletRequest request) {
        QywxUser user = (QywxUser) request.getSession().getAttribute("user");
        String isAdmin = "N";
        if (user != null) {
            isAdmin = user.getIsAdmin();
            isAdmin = StringUtils.isNotEmpty(isAdmin) ? isAdmin : "N";
        }
        model.addAttribute("isAdmin", isAdmin);
        return "qywxindex";
    }

    /**
     * 请求首页
     *
     * @return
     */
    @RequestMapping("/main")
    public String main() {
        return "main";
    }

    /**
     * 退出页面
     *
     * @return
     */

    @RequestMapping("/logout")
    public String logout() {
        //todo 此处应该是删除cookie 清空session，然后转跳到remark页，暂时先不处理
        return "qywxindex";
    }


    /**
     * 配置了 客户联系 功能的 用户列表
     *
     * @return
     */
    @RequestMapping("/followUserList")
    public String followUser() {
        return "qywx/followUser/followUser";
    }

    /**
     * 导购结果
     *
     * @return
     */
    @RequestMapping("/userManager")
    public String userManager() {
        return "qywx/userManager/main";
    }

    /**
     * 导购运营引导
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/guidance")
    public String guidance(Model model, HttpServletRequest request) {
        return "qywx/guidance/list";
    }

    /**
     * 导购发圈建议
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/friendCirlceGuide")
    public String friendCirlceGuide(Model model, HttpServletRequest request) {
        return "qywx/friends/list";
    }

    /**
     *微信推送图片
     */
    @RequestMapping("/wxMedia")
    public String wxMedia(){
        return "qywx/guideAssist/wxMediaContent";
    }

    /**
     * 标签显示
     */
    @RequestMapping("/tagList")
    public String tagList(Model model,HttpServletRequest request) {
        return "tag/tagList";
    }


}
