package com.linksteady.operate.controller;

import com.linksteady.common.annotation.Log;
import com.linksteady.common.controller.BaseController;
import com.linksteady.operate.domain.GmvPlan;
import com.linksteady.operate.domain.StateJudge;
import com.linksteady.operate.service.GmvPlanService;
import com.linksteady.operate.service.LifeCycleService;
import com.linksteady.operate.service.StateJudgeService;
import com.linksteady.system.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class PageController extends BaseController {

    @Autowired
    private StateJudgeService stateJudgeService;

    @Autowired
    private GmvPlanService gmvPlanService;

    @Autowired
    private LifeCycleService lifeCycleService;

    @RequestMapping("/page/index")
    public String index(Model model) {
        // 登录成后，即可通过 Subject 获取登录的用户信息
        User user = super.getCurrentUser();
        model.addAttribute("user", user);
        return "index";
    }

    /**
     * 用户概览
     * @return
     */
    @Log("用户概览")
    @RequestMapping("/page/stateJudge")
    public String stateJudge(Model model) {
        StateJudge stateJudge = stateJudgeService.selectAll().get(0);
        model.addAttribute("stateJudge", stateJudge);
        return "operate/overview/statejudge";
    }

    /**
     * 留存分析
     * @return
     */
    @Log("留存分析")
    @RequestMapping("/page/retention")
    public String retention() {
        return "operate/overview/retention";
    }

    /**
     * 运营目标
     * @return
     */
    @Log("目标定制与分解")
    @RequestMapping("/page/gmvplan")
    public String gmvplan() {
        return "operate/gmvplan/index";
    }

    /**
     * 运营目标新增
     */
    @Log("目标定制与分解-新增")
    @RequestMapping("/page/gmvplan/add")
    public String add() {
        return "operate/gmvplan/add";
    }

    /**
     * 运营目标查看
     * @param year
     * @param model
     * @return
     */
    @Log("目标定制与分解-查看")
    @RequestMapping("/page/gmvplan/view")
    public String gmv_view(String year, Model model) {
        model.addAttribute("year", year);
        GmvPlan gmvPlan = gmvPlanService.getByYear(year);
        model.addAttribute("gmvPlan", gmvPlan);
        return "operate/gmvplan/view";
    }

    /**
     * 运营目标变更
     * @param year
     * @param model
     * @return
     */
    @Log("目标定制与分解-变更")
    @RequestMapping("/page/gmvplan/change")
    public String change(String year, Model model) {
        model.addAttribute("year", year);
        GmvPlan gmvPlan = gmvPlanService.getByYear(year);
        model.addAttribute("gmvPlan", gmvPlan);
        return "operate/gmvplan/change";
    }


    /**
     * 运营目标编辑
     * @param year
     * @param model
     * @return
     */
    @Log("目标定制与分解-编辑")
    @RequestMapping("/page/gmvplan/edit")
    public String edit(String year, Model model) {
        model.addAttribute("year", year);
        GmvPlan gmvPlan = gmvPlanService.getByYear(year);
        model.addAttribute("gmvPlan", gmvPlan);
        return "operate/gmvplan/edit";
    }

    /**
     * 核心指标概况
     */
    @Log("核心指标监控-月")
    @RequestMapping("/page/coreindicators")
    public String coreIndicatorsMonth() {
        return "operate/coreIndicators/month";
    }

    @Log("核心指标监控-年")
    @RequestMapping("/page/coreindicators/year")
    public String coreIndicatorsYear() {
        return "operate/coreIndicators/year";
    }

    @Log("发现关键问题")
    @RequestMapping("/page/diagnosis/list")
    public String diagnosis() {
        return "operate/diagnosis/list";
    }

    @Log("发现关键问题-新增")
    @RequestMapping("/page/diagnosis/add")
    public String diagnosisAdd() {
        return "operate/diagnosis/add";
    }

    @Log("发现关键问题-查看")
    @RequestMapping("/page/diagnosis/view")
    public String diagnosisView(Model model, @RequestParam("id") String id) {
        model.addAttribute("id", id);
        return "operate/diagnosis/view";
    }

    @Log("运营指标监控")
    @RequestMapping("/page/kpimonitor")
    public String kpiMonitor() {
        return "operate/kpimonitor/index";
    }

    @Log("挖掘主要原因")
    @RequestMapping("/reason/gotoIndex")
    public String reasonIndex() {
        return "operate/reason/reasonlist";
    }

    @Log("挖掘主要原因-新增")
    @RequestMapping("/reason/add")
    public String reasonAdd() {
        return "operate/reason/reasonadd";
    }

    @Log("挖掘主要原因-查看")
    @RequestMapping("/reason/viewReason")
    public String view(@RequestParam  String reasonId, Model model) {
        model.addAttribute("reasonId", reasonId);
        return "operate/reason/reasonDetail";
    }

    /**
     * 品牌生命周期功能(暂时废弃)
     * @param model
     * @return
     */
    @RequestMapping("/lifecycle/lifecycle")
    public String lifecycleIndex(Model model) {
        StateJudge stateJudge = stateJudgeService.selectAll().get(0);
        model.addAttribute("stateJudge", stateJudge);
        return "catelifecycle";
    }

    /**
     * 品类列表
     * @param
     * @return
     */
    @Log("品类运营")
    @RequestMapping("/lifecycle/catlist")
    public String catlist(Model model) {
        StateJudge stateJudge = stateJudgeService.selectAll().get(0);
        model.addAttribute("stateJudge", stateJudge);
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

    /**
     * 运营效果
     * @param
     * @return
     */
    @RequestMapping("/op/opeffect")
    public String opEffect() {
        return "operate/op/opeffect";
    }
}
