package com.linksteady.operate.controller;

import com.google.common.collect.Maps;
import com.linksteady.common.annotation.Log;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.*;
import com.linksteady.common.service.OpenApiService;
import com.linksteady.operate.service.DiagService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/page")
public class PageController extends BaseController {

    @Autowired
    private DiagService diagService;

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Value("${app.version}")
    private String version;

    /**
     * 目标设定与分解
     * @return
     */
    @Log("目标设定与分解")
    @RequestMapping("/target")
    public String target() {
        return "operate/targetinfo/index";
    }

    /**
     * 目标设定与分解
     */
    @Log("目标设定与分解-新增")
    @RequestMapping("/target/add")
    public String add() {
        return "operate/targetinfo/add";
    }

    /**
     * 指标异常监控
     */
    @Log("指标异常监控")
    @RequestMapping("/coreindicators")
    public String coreIndicatorsMonth() {
        return "operate/coreIndicators/index";
    }

    @Log("寻找关键问题")
    @RequestMapping("/diagnosis")
    public String diagnosis() {
        return "operate/diagnosis/list";
    }

    @Log("目标设定与分解-新增")
    @RequestMapping("/diagnosis/add")
    public String diagnosisAdd() {
        return "operate/diagnosis/add";
    }

    @Log("目标设定与分解-查看")
    @RequestMapping("/diagnosis/view")
    public String diagnosisView(Model model, @RequestParam("id") String id) {
        model.addAttribute("id", id);
        return "operate/diagnosis/view";
    }

    /**
     * 诊断编辑页面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/diagnosis/edit")
    public String diagEdit(@RequestParam("id") String id, Model model) {
        Map<String, Object> diag = diagService.geDiagInfoById(id);
        model.addAttribute("diag", diag);
        model.addAttribute("id", id);
        return "operate/diagnosis/edit";
    }

    @Log("运营指标监控")
    @RequestMapping("/kpimonitor")
    public String kpiMonitor() {
        return "operate/kpimonitor/index";
    }

    @Log("寻找关键原因")
    @RequestMapping("/reason")
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

    @Log("寻找关键原因-查看效果跟踪")
    @RequestMapping("/reasonResultTrace")
    public String reasonResultTrace() {
        return "operate/reason/reasonResultTrace";
    }

    /**
     * 品牌生命周期功能(暂时废弃)
     * @param model
     * @return
     */
    @RequestMapping("/lifecycle/lifecycle")
    public String lifecycleIndex(Model model) {
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

    @RequestMapping("/operator/user")
    public String userOperator() {
        return "operate/useroperator/monitor";
    }

    /**
     * 报表 - 店铺运营日报
     * @param
     * @return
     */
    @RequestMapping("/report/opDaillyReport")
    public String opDaillyReport() {
        return "operate/report/opDaillyReport";
    }

    /**
     * 报表 - 品牌报表
     * @param
     * @return
     */
    @RequestMapping("/report/brandReport")
    public String brandReport() {
        return "operate/report/brandReport";
    }

    /**
     * 报表 - 会员日报
     * @param
     * @return
     */
    @RequestMapping("/report/userDailyReport")
    public String userDailyReport() {
        return "operate/report/userDailyReport";
    }
}
