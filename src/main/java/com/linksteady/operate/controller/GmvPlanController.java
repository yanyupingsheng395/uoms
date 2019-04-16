package com.linksteady.operate.controller;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.ArithUtil;
import com.linksteady.operate.domain.GmvPlan;
import com.linksteady.operate.domain.PlanDetail;
import com.linksteady.operate.domain.WeightIndex;
import com.linksteady.operate.domain.YearHistory;
import com.linksteady.operate.service.GmvPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gmvplan")
public class GmvPlanController extends BaseController {

    @Autowired
    private GmvPlanService gmvPlanService;

    @RequestMapping("/list")
    public Map<String, Object> list(@RequestBody QueryRequest request) {
        Map<String, Object> result = new HashMap<>();
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<GmvPlan> gmvPlanList = gmvPlanService.getGmvPlanList((request.getPageNum()-1)*request.getPageSize()+1, request.getPageNum()*request.getPageSize());
        int total = gmvPlanService.getDataCount();
        result.put("rows", gmvPlanList);
        result.put("total", total);
        return result;
    }

    @RequestMapping("/getYearHistory")
    public List<YearHistory> getYearHistory(@RequestParam("year") String year) {
        return gmvPlanService.getYearHistory(year);
    }

    @RequestMapping("/getPredictData")
    public ResponseBo getPredictData(@RequestParam("year") String year) {
        //todo 获取GMV的预测值 后续从python提供的接口中取

        //返回结果的包装结构
        Map<String,Object> result= Maps.newHashMap();

        //获取上一年的GMV值  然后按10%计算增长率
        Double gmvValue=gmvPlanService.getGmvByYear(String.valueOf(Integer.parseInt(year)-1));

        Double targetValue=0d;
        if(null!=gmvValue&&gmvValue.intValue()!=0)
        {
            targetValue= ArithUtil.formatDouble(ArithUtil.mul(gmvValue,1.1d),0);

            result.put("targetvalue",targetValue);
            result.put("targetRate","10");
            return ResponseBo.ok(result);
        }else
        {
            return ResponseBo.error("");
        }
    }

    @RequestMapping("/getWeightIndex")
    public List<WeightIndex> getWeightIndex(@RequestParam("year") String year) {
        return gmvPlanService.getWeightIndex(String.valueOf(Integer.parseInt(year)-1));
    }

    @RequestMapping("/getPlanDetail")
    public List<PlanDetail> getPlanDetail(@RequestParam("year") String year) {
        return gmvPlanService.getPlanDetail(year);
    }

    @RequestMapping("/addPlanAndDetail")
    public void addPlanAndDetail(@RequestParam("year") String year, @RequestParam("gmv") String gmv, @RequestParam("rate") String rate,@RequestParam("forecastGmv") String forecastGmv,@RequestParam("forecastRate") String forecastRate) {
        gmvPlanService.addPlanAndDetail(year, gmv, rate,forecastGmv,forecastRate);
    }

    @RequestMapping("/getPlanAndDetail")
    public ResponseBo getPlanAndDetail(@RequestParam("year") String year) {
        boolean flag = gmvPlanService.getPlanAndDetail(year);
        return ResponseBo.ok(flag);
    }

    @RequestMapping("/updateDetail")
    public ResponseBo updateDetail(@RequestParam("year") String year, @RequestParam("gmv") String gmv, @RequestParam("method") String method) {
        // todo
        // method 即：变更数据的策略 ==1：策略1； ==2：策略2
        JSONArray jsonArray = JSONArray.parseArray(gmv);
        gmvPlanService.updateDetail(jsonArray);
        return ResponseBo.ok("success！");
    }

    @PostMapping("/checkYear")
    public ResponseBo checkYear(@RequestParam("year") String year) {
        int size = gmvPlanService.checkYear(year);
        return ResponseBo.okWithData(null, size);
    }

    @PostMapping("/deleteData")
    public ResponseBo deleteData(@RequestParam("year") String year) {
        try {
            gmvPlanService.deleteDataByYear(year);
            return ResponseBo.ok();
        }catch (Exception ex) {
            ex.printStackTrace();
            return ResponseBo.error();
        }
    }

    @PostMapping("/execute")
    public ResponseBo execute(@RequestParam("id") String id) {
        try {
            gmvPlanService.execute(id);
            return ResponseBo.ok();
        }catch (Exception ex) {
            ex.printStackTrace();
            return ResponseBo.error();
        }
    }
}
