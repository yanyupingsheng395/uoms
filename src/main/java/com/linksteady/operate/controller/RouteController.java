package com.linksteady.operate.controller;

import com.linksteady.operate.domain.StateJudge;
import com.linksteady.operate.service.LifeCycleService;
import com.linksteady.operate.service.StateJudgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 负责提供导航路由的controller
 */
@Controller
@RequestMapping("/")
public class RouteController {

    @Autowired
    private StateJudgeService stateJudgeService;

    @Autowired
    private LifeCycleService lifeCycleService;

    @RequestMapping("/reason/gotoIndex")
    public String reasonIndex() {
        return "operate/reason/reasonlist";
    }

    @RequestMapping("/reason/add")
    public String reasonAdd() {
        return "operate/reason/reasonadd";
    }

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
    @RequestMapping("/lifecycle/catlist")
    public String catlist(Model model) {
        StateJudge stateJudge = stateJudgeService.selectAll().get(0);
        model.addAttribute("stateJudge", stateJudge);
        return "operate/lifecycle/cat_list";
    }

}
