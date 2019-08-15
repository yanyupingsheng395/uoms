package com.linksteady.operate.controller;

import com.linksteady.common.annotation.Log;
import com.linksteady.operate.service.DailyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
     * 每日用户成长任务配置
     * @param
     * @return
     */
    @Log("每日用户成长任务配置")
    @RequestMapping("/cfg/dailyConfig")
    public String dailyConfig() {
        return "operate/config/dailyConfig";
    }

    @RequestMapping("/effect")
    public String effect() {
        return "operate/daily/effect";
    }

    @RequestMapping("/touch")
    public String touch() {
        return "operate/daily/touch";
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

    @RequestMapping("/activity/edit")
    public String activityEdit(Model model, String id) {
        model.addAttribute("headId", id);
        return "operate/activity/edit";
    }

    @RequestMapping("/activity/add")
    public String activityAdd() {
        return "operate/activity/add";
    }
}