package com.linksteady.operate.controller;

import com.linksteady.common.annotation.Log;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.User;
import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.service.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
/**
 * @author hxcao
 * @date 2019-07-19
 */
@Controller
@RequestMapping("/page")
public class PageController extends BaseController {

    @Autowired
    private DailyService dailyService;

    @Autowired
    private DailyConfigService dailyConfigService;

    @Autowired
    private ActivityHeadService activityHeadService;

    @Autowired
    private PushProperties pushProperties;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ActivityPlanService activityPlanService;

    @Log(value = "用户成长监控",location = "用户成长系统")
    @RequestMapping("/operator/user")
    public String userOperator() {
        return "operate/useroperator/monitor";
    }

    /**
     * 品类列表
     * @param
     * @return
     */
    @Log(value="品类运营",location = "用户成长系统")
    @RequestMapping("/lifecycle/catlist")
    public String catlist() {
        return "operate/lifecycle/cat_list";
    }


//    /**
//     * 日运营列表
//     * @param
//     * @return
//     */
//    @Log("每日用户运营")
//    @RequestMapping("/op/day")
//    public String opDayList() {
//        return "operate/op/opday";
//    }

//    /**
//     * 周期运营列表
//     * @param
//     * @return
//     */
//    @Log("周期运营")
//    @RequestMapping("/op/period")
//    public String opPeriodList() {
//        return "operate/op/opperiod";
//    }

    @Log(value = "每日用户运营",location = "用户成长系统")
    @RequestMapping("/daily/task")
    public String daily() {
        return "operate/daily/list";
    }

    @Log(value = "每日运营配置",location = "用户成长系统")
    @RequestMapping("/daily/config")
    public String dailyGroupConfig(Model model) {
        model.addAttribute("shortUrlLen", pushProperties.getShortUrlLen());
        model.addAttribute("couponNameLen", pushProperties.getCouponNameLen());
        model.addAttribute("prodNameLen", pushProperties.getProdNameLen());
        model.addAttribute("couponSendType", pushProperties.getCouponSendType());
        model.addAttribute("smsLengthLimit", pushProperties.getSmsLengthLimit());
        model.addAttribute("validUrl", pushProperties.getCouponSendType());
        return "operate/daily/config";
    }

    /**
     * 每日运营-任务执行
     * @param model
     * @param headId
     * @return
     */
    @Log(value = "每日用户运营-任务执行",location = "用户成长系统")
    @RequestMapping("/daily/edit")
    public String dailyEdit(Model model, @RequestParam("id") String headId) {

        //校验
        if(StringUtils.isEmpty(headId))
        {
            model.addAttribute("errormsg","非法请求，请通过界面进行操作!");
            return "operate/daily/list";
        }else
        {
            DailyHead dailyHead=dailyService.getDailyHeadById(headId);

            if(null==dailyHead)
            {
                model.addAttribute("errormsg","不存在的任务,请通过界面进行操作!");
                return "operate/daily/list";
            }

            String currDay=LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMdd"));

            if(!"todo".equals(dailyHead.getStatus()))
            {
                model.addAttribute("errormsg","只有待执行状态的任务才能提交执行!");
                return "operate/daily/list";
            }

            if(!currDay.equals(dailyHead.getTouchDtStr()))
            {
                model.addAttribute("errormsg","只有当天的任务才能被执行!");
                return "operate/daily/list";
            }

            //验证成长组是否通过
            if(dailyConfigService.validUserGroup())
            {
                model.addAttribute("errormsg","成长组配置验证未通过!");
                return "operate/daily/list";
            }

            model.addAttribute("headId", headId);
            return "operate/daily/edit";
        }
    }

//    /**
//     * 活动短信模板配置
//     * @param
//     * @return
//     */
//    @Log("活动短信模板列表")
//    @RequestMapping("/cfg/activitySmsTemplate")
//    public String activitySmsTemplate() {
//        return "operate/config/activitySmsTemplate";
//    }

    /**
     * 短信模板配置
     * @param
     * @return
     */
    @Log(value = "每日运营文案",location = "用户成长系统")
    @RequestMapping("/cfg/smsTemplate")
    public String smsTemplateList(Model model) {
        model.addAttribute("shortUrlLen", pushProperties.getShortUrlLen());
        model.addAttribute("couponNameLen", pushProperties.getCouponNameLen());
        model.addAttribute("prodNameLen", pushProperties.getProdNameLen());
        model.addAttribute("couponSendType", pushProperties.getCouponSendType());
        model.addAttribute("smsLengthLimit", pushProperties.getSmsLengthLimit());
        return "operate/config/smstemplate";
    }


    /**
     * 优惠券配置
     * @param
     * @return
     */
    @Log(value = "每日运营优惠券",location = "用户成长系统")
    @RequestMapping("/cfg/coupon")
    public String couponList(Model model) {
        model.addAttribute("validUrl", pushProperties.getCouponSendType());
        model.addAttribute("couponNameLen", pushProperties.getCouponNameLen());
        return "operate/config/coupon";
    }

//    /**
//     * 运营配置
//     * @param
//     * @return
//     */
//    @Log("运营配置")
//    @RequestMapping("/cfg/dailyConfig")
//    public String dailyConfig() {
//        return "operate/config/dailyConfig";
//    }

    /**
     * 推送设置
     * @param
     * @return
     */
    @Log(value = "推送设置",location = "用户成长系统")
    @RequestMapping("/cfg/pushConfigList")
    public String pushConfigList() {
        return "operate/config/pushConfigList";
    }

    /**
     * 日运营-效果跟踪
     * @return
     */
    @Log(value = "每日用户运营-任务效果",location = "用户成长系统")
    @RequestMapping("daily/effect")
    public String effectTrack(Model model, @RequestParam("id") String headId) {
        String status = dailyService.getDailyHeadById(headId).getStatus();
        if(StringUtils.isNotEmpty(status)) {
            if(status.equals("done") || status.equals("finished") || status.equals("doing")) {
                model.addAttribute("headId", headId);
                DailyHead dailyHead = dailyService.getEffectById(headId);
                if(dailyHead != null) {
                    model.addAttribute("dailyHead", dailyHead);
                }else {
                    return "redirect:/page/daily/task";
                }
                return "operate/daily/effect";
            }
        }
        return "redirect:/page/daily/task";
    }
    /**
     * 活动运营
     * @return
     */
    @RequestMapping("/activity")
    @Log(value = "活动运营",location = "用户成长系统")
    public String activity() {
        return "operate/activity/list";
    }

    /**
     * 活动运营新增
     * @param model
     * @return
     */
    @RequestMapping("/activity/add")
    @Log(value = "活动运营-新增",location = "用户成长系统")
    public String activityAdd(Model model)
    {
        model.addAttribute("operateType", "save");
        model.addAttribute("prodNameLen", pushProperties.getProdNameLen());
        model.addAttribute("priceLen", pushProperties.getPriceLen());
        model.addAttribute("prodUrlLen", pushProperties.getShortUrlLen());
        model.addAttribute("smsLenLimit", pushProperties.getSmsLengthLimit());
        return "operate/activity/add/add";
    }

    /**
     * 短信推送列表页
     * @return
     */
    @Log(value = "推送记录",location = "用户成长系统")
    @RequestMapping("/push")
    public String push() {
        return "operate/push/list";
    }

    /**
     * 活动编辑
     * @param headId
     * @param model
     * @return
     */
    @Log(value = "活动用户运营-编辑",location = "用户成长系统")
    @RequestMapping("/activity/edit")
    public String activityEdit(@RequestParam("headId") Long headId, Model model) {
        ActivityHead activityHead = activityHeadService.findById(headId);
        String preheatStatus = activityHead.getPreheatStatus();
        String preheatNotifyStatus = activityHead.getPreheatNotifyStatus();
        String formalStatus = activityHead.getFormalStatus();
        String formalNotifyStatus = activityHead.getFormalNotifyStatus();
        model.addAttribute("activityHead", activityHead);
        model.addAttribute("operateType", "update");
        // 当处于done状态的时候，按钮不显示
        model.addAttribute("preheatStatus", preheatStatus);
        model.addAttribute("preheatNotifyStatus", preheatNotifyStatus);
        model.addAttribute("formalStatus", formalStatus);
        model.addAttribute("formalNotifyStatus", formalNotifyStatus);

        model.addAttribute("prodNameLen", 10);
        model.addAttribute("priceLen", 5);
        model.addAttribute("prodUrlLen", 23);
        model.addAttribute("smsLenLimit", 61);
        return "operate/activity/add/add";
    }

    /**
     * @Log("活动用户运营-查看")
     * @param headId
     * @param model
     * @return
     */
    @RequestMapping("/activity/view")
    public String activityView(@RequestParam("headId") Long headId, Model model) {
        ActivityHead activityHead = activityHeadService.findById(headId);
        String preheatStatus = activityHead.getPreheatStatus();
        String preheatNotifyStatus = activityHead.getPreheatNotifyStatus();
        String formalStatus = activityHead.getFormalStatus();
        String formalNotifyStatus = activityHead.getFormalNotifyStatus();
        model.addAttribute("activityHead", activityHead);
        model.addAttribute("operateType", "view");
        // 当处于done状态的时候，按钮不显示
        model.addAttribute("preheatStatus", preheatStatus);
        model.addAttribute("preheatNotifyStatus", preheatNotifyStatus);
        model.addAttribute("formalStatus", formalStatus);
        model.addAttribute("formalNotifyStatus", formalNotifyStatus);

        model.addAttribute("prodNameLen", 10);
        model.addAttribute("priceLen", 5);
        model.addAttribute("prodUrlLen", 23);
        model.addAttribute("smsLenLimit", 61);
        return "operate/activity/view/view";
    }

    /**
     *  活动用户运行 -执行计划
     * @param model
     * @param id
     * @return
     */
    @Log(value = "活动用户运营-执行计划",location = "用户成长系统")
    @RequestMapping("/activity/plan")
    public String activityPlan(Model model, @RequestParam String id)
    {
        int count = activityHeadService.getActivityStatus(id);
        if(count == 0) {
            return "redirect:/page/activity";
        }
        model.addAttribute("headId", id);
        return "operate/activity/plan";
    }

    /**
     * 活动运营-效果统计页
     * @return
     */
    @Log(value = "活动用户运营-任务效果",location = "用户成长系统")
    @RequestMapping("/activity/effect")
    public String activityEffect(@RequestParam("headId") String headId, Model model) {
        model.addAttribute("headId", headId);
        return "operate/activity/effect";
    }

    /**
     * 活动运营-计划效果
     * @return
     */
    @Log(value = "活动用户运营-执行计划效果",location = "用户成长系统")
    @RequestMapping("/activity/planEffect")
    public String planEffect(@RequestParam("planId") Long planId, Model model) {
        ActivityPlan activityPlan=activityPlanService.getPlanInfo(planId);

        if(null==activityPlan)
        {
            return "redirect:/page/activity";
        }else if("N".equals(activityPlan.getEffectFlag()))
        {
          //转跳到计划列表页
            model.addAttribute("headId", activityPlan.getHeadId());
            return "operate/activity/plan";

        }

        model.addAttribute("planType", activityPlan.getPlanType());
        model.addAttribute("planId", planId);

        return "operate/activity/planEffect";
    }

    /**
    * 从活动计划效果页返回到 活动计划列表  (此url不用加log)
     */
    @RequestMapping("/activity/backToPlanList")
    public String backToPlanList(Model model, @RequestParam Long planId)
    {
        ActivityPlan activityPlan=activityPlanService.getPlanInfo(planId);
        model.addAttribute("headId", activityPlan.getHeadId());
        return "operate/activity/plan";
    }


    /**
     * 用户成长洞察页
     * @return
     */
    @Log(value = "用户成长洞察",location = "用户成长系统")
    @RequestMapping("/insight")
    public String insight() {
        return "operate/insight/insight";
    }

    /**
     * 手动短信推送
     * @return
     */
    @Log(value = "手工活动推送",location = "用户成长系统")
    @RequestMapping("/manual")
    public String manual(Model model) {
        model.addAttribute("fontNum", pushProperties.getSmsLengthLimit());
        return "operate/manual/manual";
    }

    /**
     * 单一用户的成长洞察
     */
    @RequestMapping("/personInsight")
    @Log(value = "单一用户成长洞察",location = "用户成长系统")
    public String personInsight(@RequestParam("userId") String userId, @RequestParam("headId") String headId, Model model) {
        model.addAttribute("userId", userId);
        model.addAttribute("headId", headId);
        model.addAttribute("pathActive", configService.getValueByName("op.daily.pathactive.list"));
        return "operate/daily/person_insight";
    }

    /**
     * 系统消息
     * @return
     */
    @RequestMapping("/msg")
    @Log(value = "系统消息",location = "用户成长系统")
    public String msgPage() {
        return "operate/msg/list";
    }

    /**
     * 短链生成页面
     * @return
     */
    @RequestMapping("/shorturl")
    @Log(value = "短链生成",location = "用户成长系统")
    public String shorturl() {
        return "operate/shorturl/shorturl";
    }
}