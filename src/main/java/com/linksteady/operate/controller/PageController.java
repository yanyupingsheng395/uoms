package com.linksteady.operate.controller;

import com.linksteady.common.annotation.Log;
import com.linksteady.common.controller.BaseController;
import com.linksteady.operate.domain.StateJudge;
import com.linksteady.operate.service.StateJudgeService;
import com.linksteady.system.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/page")
public class PageController extends BaseController {

    @Autowired
    private StateJudgeService stateJudgeService;


    @RequestMapping("/")
    public String redirectIndex() {
        return "redirect:/index";
    }

    @GetMapping("/403")
    public String forbid() {
        return "403";
    }

    @Log("访问系统")
    @RequestMapping("/index")
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
    @RequestMapping("/stateJudge")
    public String stateJudge(Model model) {
        StateJudge stateJudge = stateJudgeService.selectAll().get(0);
        model.addAttribute("stateJudge", stateJudge);
        return "operate/overview/statejudge";
    }

    /**
     * 留存分析
     * @return
     */
    @RequestMapping("/retention")
    public String retention() {
        return "operate/overview/retention";
    }

    /**
     * 运营目标
     * @return
     */
    @RequestMapping("/gmvplan")
    public String gmvplan() {
        return "operate/gmvplan/index";
    }

    /**
     * 运营目标新增
     */
    @RequestMapping("/gmvplan/add")
    public String add() {
        return "operate/gmvplan/add";
    }

    /**
     * 运营目标查看
     * @param year
     * @param model
     * @return
     */
    @RequestMapping("/gmvplan/view")
    public String view(String year, Model model) {
        model.addAttribute("year", year);
        return "operate/gmvplan/view";
    }

    /**
     * 运营目标变更
     * @param year
     * @param model
     * @return
     */
    @RequestMapping("/gmvplan/change")
    public String change(String year, Model model) {
        model.addAttribute("year", year);
        return "operate/gmvplan/change";
    }


    /**
     * 运营目标编辑
     * @param year
     * @param model
     * @return
     */
    @RequestMapping("/gmvplan/edit")
    public String edit(String year, Model model) {
        model.addAttribute("year", year);
        return "operate/gmvplan/edit";
    }

    /**
     * 核心指标概况
     */
    @RequestMapping("/coreindicators")
    public String coreIndicators_month() {
        return "operate/coreIndicators/month";
    }

    @RequestMapping("/coreindicators/year")
    public String coreIndicators_year() {
        return "operate/coreIndicators/year";
    }

    @RequestMapping("/diagnosis/list")
    public String diagnosis() {
        return "operate/diagnosis/list";
    }
}
