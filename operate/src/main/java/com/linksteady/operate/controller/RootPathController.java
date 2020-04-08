package com.linksteady.operate.controller;

import com.google.common.collect.Maps;
import com.linksteady.common.bo.UserBo;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.*;
import com.linksteady.common.service.CommonFunService;
import com.linksteady.common.util.MD5Utils;
import com.linksteady.lognotice.service.ExceptionNoticeHandler;
import com.linksteady.operate.domain.PushProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-07-19
 */
@Slf4j
@Controller
@RequestMapping("/")
public class RootPathController extends BaseController {

    @Autowired
    CommonFunService commonFunService;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @Autowired
    private PushProperties pushProperties;

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

    @RequestMapping("/")
    public String root() {
        return "redirect:index";
    }

    @RequestMapping("/index")
    public String index(Model model) {
        // 登录成后，即可通过 Subject 获取登录的用户信息
        UserBo userBo = super.getCurrentUser();
        model.addAttribute("user", userBo);
        model.addAttribute("version", version);
        return "index";
    }

    @RequestMapping("/sysinfo")
    @ResponseBody
    public ResponseBo getSysInfo() {
        String username = ((UserBo) SecurityUtils.getSubject().getPrincipal()).getUsername();
        Map result= Maps.newHashMap();
        result.put("appname",appname);
        result.put("version",version);
        result.put("appdesc",appdesc);
        result.put("buildtime",buildTime);
        result.put("bootversion",bootversion);
        result.put("currentUser",username);
        return ResponseBo.okWithData("",result);
    }

    /**
     * 获取登录用户的菜单
     * @return
     */
    @RequestMapping("/findUserMenu")
    @ResponseBody
    public ResponseBo getUserMenu() {
        UserBo userBo = super.getCurrentUser();
        SysInfoBo sysInfoBo=commonFunService.getSysInfoByCode("operate");

        if(null==sysInfoBo)
        {
            return ResponseBo.error("");
        }

        //返回的数据集
        Map<String, Object> result = new HashMap<>(16);
        String userName = userBo.getUsername();
        result.put("username", userName);
        result.put("version", version);
        String sysDomain = sysInfoBo.getSysDomain();
        result.put("navigatorUrl", sysDomain + "/main");
        result.put("logoutUrl", sysDomain + "/logout");
        result.put("single", userBo.getUserMenuTree().keySet().size() == 1);

        try {
            Tree<Menu> tree = userBo.getUserMenuTree().get(sysInfoBo.getSysCode());
            result.put("tree", tree);
            return ResponseBo.okWithData(result,sysInfoBo.getSysName());
        } catch (Exception e) {
            log.error("获取用户菜单失败", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e),1,512));
            return ResponseBo.error("获取用户菜单失败！");
        }
    }

    /**
     * 推送控制页
     * @return
     */
    @RequestMapping("/push")
    public String push(Model model) {
        if(pushProperties != null) {
            String status = pushProperties.getPushFlag();
            if(StringUtils.isNotEmpty(status)) {
                model.addAttribute("status", status);
            }
        }
        return "operate/push/dashbord";
    }

    /**
     * 桑基图
     * @return
     */
    @RequestMapping("/sankey")
    public String sankeyChart(@RequestParam("dateRange") String dateRange, Model model) {
        model.addAttribute("dateRange", dateRange);
        return "operate/insight/sankey";
    }

    @RequestMapping("/fullScreen")
    public String fullScreen() {
        return "operate/insight/fullscreen";
    }
}