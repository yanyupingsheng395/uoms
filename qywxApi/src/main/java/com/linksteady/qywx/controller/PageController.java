package com.linksteady.qywx.controller;

import com.linksteady.common.annotation.Log;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.service.ConfigService;
import com.linksteady.qywx.domain.AddUserHead;
import com.linksteady.qywx.domain.FollowUser;
import com.linksteady.qywx.domain.QywxParam;
import com.linksteady.qywx.domain.QywxWelcome;
import com.linksteady.qywx.service.*;
import com.linksteady.qywx.vo.SmsConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/page")
@Slf4j
public class PageController  extends BaseController {

    @Autowired
    WelcomeService welcomeService;

    @Autowired
    private AddUserService addUserService;

    @Autowired
    private QywxParamService qywxParamService;


    @Autowired
    private AddUserTriggerService addUserTriggerService;

    @Autowired
    ConfigService configService;

    @Autowired
    FollowUserService followUserService;


    /**
     * 企业微信应用配置
     */
    @RequestMapping("/qywxSetting")
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
        return "qywx/image/imgList";
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
    @RequestMapping("/contactWay")
    public String contactWayList() {
        return "qywx/contactWay/list";
    }


    /**
     * 新增渠道渠道活码
     */
    @RequestMapping("/contactWay/add")
    public String addContactWay(Model model,String contactWayId) {
        model.addAttribute("contactWayId",contactWayId);
        return "qywx/contactWay/add";
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
    public String editWelcome(Model model, long id) {
        QywxWelcome qywxWelcome = welcomeService.getDataById(id);
        model.addAttribute("welcome", qywxWelcome);
        model.addAttribute("msgType", qywxWelcome.getMsgType());
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
        SmsConfigVO smsConfigVO = SmsConfigVO.getInstance(configService);
        model.addAttribute("smsConfigVO", smsConfigVO);
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
            SmsConfigVO smsConfigVO = SmsConfigVO.getInstance(configService);
            model.addAttribute("smsConfigVO", smsConfigVO);
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
        SmsConfigVO smsConfigVO = SmsConfigVO.getInstance(configService);
        model.addAttribute("smsConfigVO", smsConfigVO);
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
            SmsConfigVO smsConfigVO = SmsConfigVO.getInstance(configService);
            model.addAttribute("smsConfigVO", smsConfigVO);
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
     * 企业微信上传图片列表(临时)
     * @return
     */
    @RequestMapping("/qywxMedia")
    public String qywxMedia(){
        return "qywx/qywxMedia/qywxImgList";
    }

    /**
     * 进入客户群联系列表界面
     * @return
     */
    @RequestMapping("/goCustomerBase")
    public String goCustomerBase(){
        return  "qywx/qywxChat/Baselist";
    }

    /**
     * 进入客户群详情页
     * @return
     */
    @RequestMapping("/goCustomerBase/goChatDetail/{chatId}")
    public String goChatDetail(Model model, @PathVariable(name = "chatId") String chatId){
        model.addAttribute("chatId",chatId);
        return  "qywx/qywxChat/ChatDetail";
    }


    /**
     * 进入群发消息列表页
     * @return
     */
    @RequestMapping("/goChatMsgList")
    public String goChatMsgList(){
        return  "qywx/qywxChatMessage/groupMessageList";
    }

    /**
     * 进入群发消息
     * @return
     */
    @RequestMapping("/goChatMsgList/add")
    public String goBaseMsg(Model model){
       List<FollowUser> foll= followUserService.getFollowUserList();
       model.addAttribute("foll",foll);
        return  "qywx/qywxChatMessage/BaseSendMsg";
    }

    /**
     * 群SOP
     * @return
     */
    @RequestMapping("/goBaseSop")
    public String goBaseSop(){
        return  "qywx/groupSOP/BaseSOP";
    }

    /**
     * 进入自动拉群界面
     * @return
     */
    @RequestMapping("/goPullGroup")
    public String goPullGroup(){
        return  "qywx/autoPullGroup/PullGroup";
    }

    /**
     * 进入新建拉群界面
     * @return
     */
    @RequestMapping("/goAddGroup")
    public String goAddGroup(){
        return  "qywx/autoPullGroup/addGroup";
    }

    /**
     * 进入标签建群界面
     * @return
     */
    @RequestMapping("/goTagGroup")
    public String goTagGroup(){
        return  "qywx/tagGroup/tagGroupList";
    }

    /**
     * 进入添加标签群界面
     * @return
     */
    @RequestMapping("/addTagGroup")
    public String addTagGroup(){
        return  "qywx/tagGroup/addTagGroup";
    }

    /**
     * 进入群日历列表界面
     * @return
     */
    @RequestMapping("/goGroupCalendar")
    public String goGroupCalendar(){
        return "qywx/groupCalendar/groupCalendarList";
    }

    /**
     * 进入添加群日历界面
     * @return
     */
    @RequestMapping("/addGroupCalendar")
    public String addGroupCalendar(){
        return "qywx/groupCalendar/addGroupCalendar";
    }

    /**
     * 进入标签组管理界面
     * @return
     */
    @RequestMapping("/groupTag")
    public String groupTag(){
        return "qywx/groupTag/groupTagList";
    }


}
