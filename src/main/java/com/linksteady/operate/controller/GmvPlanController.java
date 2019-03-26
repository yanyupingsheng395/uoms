package com.linksteady.operate.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.operate.domain.GmvPlan;
import com.linksteady.operate.domain.PlanDetail;
import com.linksteady.operate.domain.WeightIndex;
import com.linksteady.operate.domain.YearHistory;
import com.linksteady.operate.service.GmvPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/gmvplan")
public class GmvPlanController extends BaseController {

    @Autowired
    private GmvPlanService gmvPlanService;

    @RequestMapping("/index")
    public String index() {
        return "operate/gmvplan/index";
    }

    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> list(QueryRequest request) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<GmvPlan> gmvPlanList = gmvPlanService.selectAll();
        PageInfo<GmvPlan> pageInfo = new PageInfo<>(gmvPlanList);
        return getDataTable(pageInfo);
    }

    @RequestMapping("/add")
    public String add() {
        return "operate/gmvplan/add";
    }

    @RequestMapping("/getYearHistory")
    @ResponseBody
    public List<YearHistory> getYearHistory(@RequestParam("year") String year) {
        return gmvPlanService.getYearHistory(year);
    }

    @RequestMapping("/getPredictData")
    @ResponseBody
    public List<GmvPlan> getPredictData(@RequestParam("year") String year) {
        return gmvPlanService.getPredictData(year);
    }

    @RequestMapping("/getWeightIndex")
    @ResponseBody
    public List<WeightIndex> getWeightIndex(@RequestParam("year") String year) {
        return gmvPlanService.getWeightIndex(year);
    }

    @RequestMapping("/getPlanDetail")
    @ResponseBody
    public List<PlanDetail> getPlanDetail(@RequestParam("year") String year) {
        return gmvPlanService.getPlanDetail(year);
    }

    @RequestMapping("/modify")
    public String modify() {
        return "operate/gmvplan/edit";
    }

    @RequestMapping("/change")
    public String change() {
        return "operate/gmvplan/change";
    }
}
