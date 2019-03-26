package com.linksteady.operate.controller;

import com.linksteady.common.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/stateJudge")
public class StateJudgeController extends BaseController {

    @RequestMapping("/index")
    public String index() {
        return "operate/overview/statejudge";
    }

    @RequestMapping("/tableList")
    public Map<String, Object> tableList() {

        return  null;
    }
}
