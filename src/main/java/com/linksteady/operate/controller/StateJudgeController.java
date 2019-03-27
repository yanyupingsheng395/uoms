package com.linksteady.operate.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.operate.domain.StateJudge;
import com.linksteady.operate.service.StateJudgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/stateJudge")
public class StateJudgeController extends BaseController {

    @Autowired
    private StateJudgeService stateJudgeService;

    @RequestMapping("/index")
    public String index(Model model) {
        StateJudge stateJudge = stateJudgeService.selectAll().get(0);
        model.addAttribute("stateJudge", stateJudge);
        return "operate/overview/statejudge";
    }
}