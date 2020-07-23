package com.linksteady.operate.controller;

import com.linksteady.common.annotation.Log;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.config.PushConfig;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.domain.enums.ConfigEnum;
import com.linksteady.operate.service.ActivityHeadService;
import com.linksteady.operate.service.ActivityPlanService;
import com.linksteady.operate.service.DailyService;
import com.linksteady.operate.service.QywxDailyService;
import com.linksteady.operate.vo.SourceConfigVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

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
    private ActivityHeadService activityHeadService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ActivityPlanService activityPlanService;

    @Autowired
    private QywxDailyService qywxDailyService;

    @Autowired
    PushConfig pushConfig;

    @Log(value = "用户成长监控",location = "用户成长系统")
    @RequestMapping("/operator/user")
    public String userOperator() {
        return "operate/useroperator/monitor";
    }


    @Log(value = "每日用户运营",location = "用户成长系统")
    @RequestMapping("/daily/task")
    public String daily() {
        return "operate/daily/list";
    }

    @Log(value = "每日运营配置",location = "用户成长系统")
    @RequestMapping("/dailyconfig")
    public String dailyGroupConfig(Model model) {
        SourceConfigVO sourceConfigVO=SourceConfigVO.getInstance(pushConfig);
        model.addAttribute("sourceConfig",sourceConfigVO);
        return "operate/dailyconfig/config";
    }



    /**
     * 短信模板配置
     * @param
     * @return
     */
    @Log(value = "每日运营文案",location = "用户成长系统")
    @RequestMapping("/cfg/smsTemplate")
    public String smsTemplateList(Model model) {
        SourceConfigVO sourceConfigVO=SourceConfigVO.getInstance(pushConfig);
        model.addAttribute("sourceConfig",sourceConfigVO);
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
        SourceConfigVO sourceConfigVO=SourceConfigVO.getInstance(pushConfig);
        model.addAttribute("sourceConfig",sourceConfigVO);
        return "operate/config/coupon";
    }

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
    public String effectTrack(Model model, @RequestParam("id") Long headId) {
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
        SourceConfigVO sourceConfigVO=SourceConfigVO.getInstance(pushConfig);
        model.addAttribute("sourceConfig",sourceConfigVO);
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
        List<ActivityCoupon> activityCouponList = activityHeadService.findCouponList(headId);
        List<ActivityCoupon> platCouponList = activityCouponList.stream().filter(x->x.getCouponType().equalsIgnoreCase("P")).collect(Collectors.toList());
        List<ActivityCoupon> shopCouponList = activityCouponList.stream().filter(x->x.getCouponType().equalsIgnoreCase("S")).collect(Collectors.toList());
        String preheatStatus = activityHead.getPreheatStatus();
        String preheatNotifyStatus = activityHead.getPreheatNotifyStatus();
        String formalStatus = activityHead.getFormalStatus();
        String formalNotifyStatus = activityHead.getFormalNotifyStatus();
        model.addAttribute("activityHead", activityHead);
        model.addAttribute("platCouponList", platCouponList);
        model.addAttribute("shopCouponList", shopCouponList);
        model.addAttribute("operateType", "update");
        // 当处于done状态的时候，按钮不显示
        model.addAttribute("preheatStatus", preheatStatus);
        model.addAttribute("preheatNotifyStatus", preheatNotifyStatus);
        model.addAttribute("formalStatus", formalStatus);
        model.addAttribute("formalNotifyStatus", formalNotifyStatus);

        //文案相关变量
        SourceConfigVO sourceConfigVO=SourceConfigVO.getInstance(pushConfig);
        model.addAttribute("sourceConfig",sourceConfigVO);
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
        List<ActivityCoupon> activityCouponList = activityHeadService.findCouponList(headId);
        List<ActivityCoupon> platCouponList = activityCouponList.stream().filter(x->x.getCouponType().equalsIgnoreCase("P")).collect(Collectors.toList());
        List<ActivityCoupon> shopCouponList = activityCouponList.stream().filter(x->x.getCouponType().equalsIgnoreCase("S")).collect(Collectors.toList());
        String preheatStatus = activityHead.getPreheatStatus();
        String preheatNotifyStatus = activityHead.getPreheatNotifyStatus();
        String formalStatus = activityHead.getFormalStatus();
        String formalNotifyStatus = activityHead.getFormalNotifyStatus();
        model.addAttribute("activityHead", activityHead);
        model.addAttribute("operateType", "view");
        model.addAttribute("platCouponList", platCouponList);
        model.addAttribute("shopCouponList", shopCouponList);
        // 当处于done状态的时候，按钮不显示
        model.addAttribute("preheatStatus", preheatStatus);
        model.addAttribute("preheatNotifyStatus", preheatNotifyStatus);
        model.addAttribute("formalStatus", formalStatus);
        model.addAttribute("formalNotifyStatus", formalNotifyStatus);

        //文案相关变量
        SourceConfigVO sourceConfigVO=SourceConfigVO.getInstance(pushConfig);
        model.addAttribute("sourceConfig",sourceConfigVO);
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
            model.addAttribute("errormsg","无已经生成的执行计划!");
            return "operate/activity/list";
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
        return "operate/manual/manual";
    }

    /**
     * 单一用户的成长洞察
     */
    @RequestMapping("/personInsight")
    @Log(value = "单一用户成长洞察",location = "用户成长系统")
    public String personInsight(@RequestParam("headId") String headId,@RequestParam("userId") String userId, @RequestParam("taskDt") String taskDt, Model model) {
        model.addAttribute("userId", userId);
        model.addAttribute("taskDt", taskDt);
        model.addAttribute("headId", headId);
        model.addAttribute("pathActive", configService.getValueByName(ConfigEnum.pathActiveList.getKeyCode()));
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

    /**
     * 黑名单
     * @return
     */
    @Log(value = "黑名单", location = "用户成长系统")
    @RequestMapping("/black")
    public String black() {
        return "operate/black/list";
    }

    /**
     * 每日用户运营(企业微信)
     */
    @Log(value = "每日用户运营(企业微信)",location = "用户成长系统")
    @RequestMapping("/qywxDaily/list")
    public String qywxDailyList() {
        return "operate/qywx/list";
    }

    /**
     * 每日运营[企业微信]-效果跟踪
     * @return
     */
    @Log(value = "每日用户运营[企业微信]-任务效果",location = "用户成长系统")
    @RequestMapping("/qywxDaily/effect")
    public String qywxDailyEffect(Model model, @RequestParam("headId") Long headId) {
        String status = qywxDailyService.getHeadInfo(headId).getStatus();
//        if(StringUtils.isNotEmpty(status)) {
//            if(status.equals("done") || status.equals("finished") || status.equals("doing")) {
//                model.addAttribute("headId", headId);
//                //DailyHead dailyHead = dailyService.getEffectById(headId);
//                QywxDailyHeader qywxDailyHeader=qywxDailyService.getHeadInfo(headId);
//                if(qywxDailyHeader != null) {
//                    model.addAttribute("qywxDailyHeader", qywxDailyHeader);
//                }else {
//                    return "redirect:/page/qywxDaily/list";
//                }
//                return "operate/qywx/effect";
//            }
//        }
//        return "redirect:/page/qywxDaily/list";

        QywxDailyHeader qywxDailyHeader=qywxDailyService.getHeadInfo(headId);
        model.addAttribute("headId", headId);
        model.addAttribute("qywxDailyHeader", qywxDailyHeader);
        return "operate/qywx/effect";
    }

    /**
     *添加外部联系人-列表
     */
    @RequestMapping("/addCustom")
    public String addCustomList() {
        return "operate/addCustom/list";
    }

    /**
     *添加外部联系人-新增
     */
    @RequestMapping("/addCustom/add")
    public String addCustomAdd() {
        return "operate/addCustom/add";
    }

    /**
     * 添加外部联系人效果
     * @return
     */
    @RequestMapping("/addCustom/effect")
    public String addCustomEffect() {
        return "operate/addCustom/effect";
    }

    /**
     * 渠道活码
     */
    @Log(value = "渠道活码",location = "用户成长系统")
    @RequestMapping("/contactWay/list")
    public String contactWayList() {
        return "operate/contactWay/list";
    }
}