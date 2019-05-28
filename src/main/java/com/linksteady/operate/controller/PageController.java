package com.linksteady.operate.controller;

import com.linksteady.common.annotation.Log;
import com.linksteady.common.controller.BaseController;
import com.linksteady.operate.domain.StateJudge;
import com.linksteady.operate.service.LifeCycleService;
import com.linksteady.operate.service.StateJudgeService;
import com.linksteady.system.domain.User;
import org.dozer.DozerBeanMapper;
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
    private LifeCycleService lifeCycleService;

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @RequestMapping("/page/index")
    public String index(Model model) {
        // 登录成后，即可通过 Subject 获取登录的用户信息
        User user = super.getCurrentUser();
        model.addAttribute("user", user);
        return "index";
    }

    /**
     * 目标设定与分解
     * @return
     */
    @Log("目标设定与分解")
    @RequestMapping("/page/target")
    public String target() {
        return "operate/targetinfo/index";
    }

    /**
     * 目标设定与分解
     */
    @Log("目标设定与分解-新增")
    @RequestMapping("/page/target/add")
    public String add() {
        return "operate/targetinfo/add";
    }

    /**
     * 指标异常监控
     */
    @Log("指标异常监控")
    @RequestMapping("/page/coreindicators")
    public String coreIndicatorsMonth() {
        return "operate/coreIndicators/index";
    }

    @Log("寻找关键问题")
    @RequestMapping("/page/diagnosis/list")
    public String diagnosis() {
        return "operate/diagnosis/list";
    }

    @Log("目标设定与分解-新增")
    @RequestMapping("/page/diagnosis/add")
    public String diagnosisAdd() {
        return "operate/diagnosis/add";
    }

    @Log("目标设定与分解-查看")
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

    @Log("寻找关键原因")
    @RequestMapping("/reason/gotoIndex")
    public String reasonIndex() {
        return "operate/reason/reasonlist";
    }

    @Log("寻找关键原因-新增")
    @RequestMapping("/reason/add")
    public String reasonAdd() {
        return "operate/reason/reasonadd";
    }

    @Log("寻找关键原因-查看")
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

    /**
     * 目标设定与分解-目标详情
     */
    @RequestMapping("/target/detail")
    public String targetDetail(@RequestParam("id") String id, Model model) {
        model.addAttribute("id", id);
        return "operate/targetinfo/detail";
    }
}
