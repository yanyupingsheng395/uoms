package com.linksteady.operate.controller;

import com.linksteady.common.annotation.Log;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.ActivityHead;
import com.linksteady.operate.domain.DailyHead;
import com.linksteady.operate.service.ActivityHeadService;
import com.linksteady.operate.service.DailyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author hxcao
 * @date 2019-07-19
 */
@Controller
@RequestMapping("/page")
public class PageController {

    @Autowired
    private DailyService dailyService;

    @Autowired
    private ActivityHeadService activityHeadService;

    @Log("用户运营监控")
    @RequestMapping("/operator/user")
    public String userOperator() {
        return "operate/useroperator/monitor";
    }

    /**
     * 品类列表
     * @param
     * @return
     */
    @Log("品类运营")
    @RequestMapping("/lifecycle/catlist")
    public String catlist(Model model) {
        return "operate/lifecycle/cat_list";
    }


    /**
     * 日运营列表
     * @param
     * @return
     */
    @Log("每日运营")
    @RequestMapping("/op/day")
    public String opDayList() {
        return "operate/op/opday";
    }

    /**
     * 周期运营列表
     * @param
     * @return
     */
    @Log("周期运营")
    @RequestMapping("/op/period")
    public String opPeriodList() {
        return "operate/op/opperiod";
    }

    @RequestMapping("/daily")
    public String daily() {
        return "operate/daily/list";
    }


    @RequestMapping("/daily/edit")
    public String dailyEdit(Model model, @RequestParam("id") String headId) {
        String status = dailyService.getStatusById(headId);
        if(StringUtils.isNotEmpty(status)) {
            if(status.equals("todo")) {
                model.addAttribute("headId", headId);
                return "operate/daily/edit";
            }
        }
        return "redirect:/page/daily";
    }

    @RequestMapping("/daily/view")
    public String dailyView(Model model, @RequestParam("id") String headId) {
        model.addAttribute("headId", headId);
        return "operate/daily/view";
    }

    /**
     * 短信模板配置
     * @param
     * @return
     */
    @Log("短信模板列表")
    @RequestMapping("/cfg/smsTemplate")
    public String smsTemplateList() {
        return "operate/config/smstemplate";
    }


    /**
     * 优惠券配置
     * @param
     * @return
     */
    @Log("短信模板列表")
    @RequestMapping("/cfg/coupon")
    public String couponList() {
        return "operate/config/coupon";
    }

    /**
     * 运营配置
     * @param
     * @return
     */
    @Log("运营配置")
    @RequestMapping("/cfg/dailyConfig")
    public String dailyConfig() {
        return "operate/config/dailyConfig";
    }

    /**
     * 运营配置预览
     * @param
     * @return
     */
    @Log("运营配置预览")
    @RequestMapping("/cfg/dailyConfigView")
    public String dailyConfigView() {
        return "operate/config/dailyConfigView";
    }

    /**
     * 步骤图
     * @return
     */
    @RequestMapping("/daily/step")
    public String step(@RequestParam String headId) {
        return "step2";
    }

    /**
     * 日运营-效果跟踪
     * @return
     */
    @RequestMapping("daily/effect")
    public String effectTrack(Model model, @RequestParam("id") String headId) {
        String status = dailyService.getStatusById(headId);
        if(StringUtils.isNotEmpty(status)) {
            if(status.equals("done") || status.equals("finished") || status.equals("doing")) {
                model.addAttribute("headId", headId);
                DailyHead dailyHead = dailyService.getEffectById(headId);
                if(dailyHead != null) {
                    model.addAttribute("dailyHead", dailyHead);
                }else {
                    return "redirect:/page/daily";
                }
                return "operate/daily/effect";
            }
        }
        return "redirect:/page/daily";
    }

    /**
     * 活动运营
     * @return
     */
    @RequestMapping("/activity")
    public String activity() {
        return "operate/activity/list";
    }

    @RequestMapping("/activity/add")
    public String activityAdd(Model model)
    {
        model.addAttribute("operateType", "save");
        return "operate/activity/add";
    }

    /**
     * 用户组模板配置表
     * @return
     */
    @RequestMapping("/usergroup")
    public String userGroup() {
        return "operate/daily/usergroup";
    }


    /**
     * 会员日成长任务列表页
     * @return
     */
    @RequestMapping("/member")
    public String memberList() {
        return "operate/member/list";
    }


    /**
     * 会员日成长任务列表页
     * @return
     */
    @RequestMapping("/member/edit")
    public String memberEdit(String id, Model model) {
        model.addAttribute("id", id);
        return "operate/member/edit";
    }

    /**
     * 短信推送列表页
     * @return
     */
    @RequestMapping("/push")
    public String push() {
        return "operate/push/list";
    }

    @RequestMapping("/activity/edit")
    public String activityEdit(@RequestParam("headId") String headId, Model model) {
        ActivityHead activityHead = activityHeadService.findById(headId);
        model.addAttribute("activityHead", activityHead);
        model.addAttribute("operateType", "update");
        return "operate/activity/add";
    }

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

}