package com.linksteady.operate.controller;

import com.google.common.collect.Maps;
import com.linksteady.common.annotation.Log;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.User;
import com.linksteady.common.service.OpenApiService;
import com.linksteady.operate.service.DiagService;
import org.apache.shiro.SecurityUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Controller
@RequestMapping("/")
public class PageController extends BaseController {

    @Autowired
    private DiagService diagService;

    @Autowired
    DozerBeanMapper dozerBeanMapper;
    /**
     * 当前系统简称
     */
    @Value("${app.name}")
    private String appname;

    /**
     * 当前版本
     */
    @Value("${app.version}")
    private String version;

    /**
     * 当前系统中文名称
     */
    @Value("${app.description}")
    private String appdesc;

    /**
     * 当前spring boot的版本
     */
    @Value("${app.spring-boot-version}")
    private String bootversion;

    /**
     * 打包时间
     */
    @Value("${app.build.time}")
    private String buildTime;

    @RequestMapping("/index")
    public String index(Model model) {
        // 登录成后，即可通过 Subject 获取登录的用户信息
        User user = super.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("version", version);
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

    @Log("寻找关键原因-查看效果跟踪")
    @RequestMapping("/reason/reasonResultTrace")
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

    /**
     * 诊断编辑页面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/diag/edit")
    public String diagEdit(@RequestParam("id") String id, Model model) {
        Map<String, Object> diag = diagService.geDiagInfoById(id);
        model.addAttribute("diag", diag);
        model.addAttribute("id", id);
        return "operate/diagnosis/edit";
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

    @RequestMapping("/sysinfo")
    @ResponseBody
    public ResponseBo getSysInfo() {
        String username = ((User)SecurityUtils.getSubject().getPrincipal()).getUsername();
        Map result= Maps.newHashMap();
        result.put("appname",appname);
        result.put("version",version);
        result.put("appdesc",appdesc);
        result.put("buildtime",buildTime);
        result.put("bootversion",bootversion);
        result.put("currentUser",username);
        return ResponseBo.okWithData("",result);
    }

    @RequestMapping("/getSysIdFromSession")
    @ResponseBody
    public ResponseBo getSysIdFromSession(HttpServletRequest request) {
        String sysId = String.valueOf(request.getSession().getAttribute("sysId"));
        return ResponseBo.okWithData(null, sysId);
    }

    @RequestMapping("/setSysIdToSession")
    @ResponseBody
    public ResponseBo setSysIdToSession(HttpServletRequest request, @RequestParam("sysId") String sysId) {
        request.getSession().setAttribute("sysId", sysId);
        return ResponseBo.ok();
    }
}
