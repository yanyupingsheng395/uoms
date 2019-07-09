package com.linksteady.jobmanager.controller;

import com.google.common.collect.Maps;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.*;
import com.linksteady.common.util.MD5Utils;
import com.linksteady.lognotice.service.ExceptionNoticeHandler;
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
 * @date 2019-07-08
 */
@Slf4j
@Controller
@RequestMapping("/")
public class RootPathController extends BaseController {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

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
        User user = super.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("version", version);
        return "index";
    }


    @RequestMapping("/setSysIdToSession")
    @ResponseBody
    public ResponseBo setSysIdToSession(HttpServletRequest request, @RequestParam("sysId") String sysId) {
        //将sysId放入session中，当获取当前用户的菜单时候使用
        request.getSession().setAttribute("sysId", sysId);
        return ResponseBo.ok();
    }

    @RequestMapping("/sysinfo")
    @ResponseBody
    public ResponseBo getSysInfo() {
        String username = ((User) SecurityUtils.getSubject().getPrincipal()).getUsername();
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
    public ResponseBo getUserMenu(HttpServletRequest request) {
        String sysId = String.valueOf(request.getSession().getAttribute("sysId"));
        User user = super.getCurrentUser();

        if(null==sysId||"".equals(sysId)||"null".equals(sysId))
        {
            return ResponseBo.error("");
        }

        //返回的数据集
        Map<String, Object> result = new HashMap<>();
        String userName = user.getUsername();
        result.put("username", userName);
        result.put("version", version);
        Map<String,String> appMap=(Map<String, String>)redisTemplate.opsForValue().get("applicationInfoMap");
        result.put("navigatorUrl",appMap.get("SYS")+"main");
        result.put("logoutUrl",appMap.get("SYS")+"logout");


        //获取当前子系统名称
        Map<String, SysInfo> sysInfoMap=(Map<String, SysInfo>)redisTemplate.opsForValue().get("sysInfoMap");
        String sysName=null==sysInfoMap.get(sysId)?"":sysInfoMap.get(sysId).getName();
        try {
            Tree<Menu> tree = user.getUserMenuTree().get(sysId);
            result.put("tree", tree);
            return ResponseBo.okWithData(result,sysName);
        } catch (Exception e) {
            log.error("获取用户菜单失败", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e),1,512));
            return ResponseBo.error("获取用户菜单失败！");
        }
    }

    @RequestMapping("user/checkPassword")
    @ResponseBody
    public boolean checkPassword(String password) {
        User user = getCurrentUser();
        String encrypt = MD5Utils.encrypt(user.getUsername().toLowerCase(), password);
        return user.getPassword().equals(encrypt);
    }
}
