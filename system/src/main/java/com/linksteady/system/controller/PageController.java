package com.linksteady.system.controller;

import com.linksteady.common.annotation.Log;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.*;
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

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/")
public class PageController extends BaseController {

    @Autowired
    RedisTemplate redisTemplate;

    @Value("${app.version}")
    private String version;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @RequestMapping("/index")
        public String index(Model model) {
        // 登录成后，即可通过 Subject 获取登录的用户信息
        User user = super.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("version", version);
        return "index";
    }

    @RequestMapping("/main")
    public String main(Model model) {
        // 登录成后，即可通过 Subject 获取登录的用户信息
        User user = super.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("version", version);
        return "main";
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

    @Log("强制修改密码")
    @RequestMapping("/resetPass")
    public String resetPass(Model model) {
        // 登录成后，即可通过 Subject 获取登录的用户信息
        User user = super.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("version", version);

        if(null!=user.getFirstLogin()&&"Y".equals(user.getFirstLogin()))
        {
            return "resetPassword";
        }else
        {
            return "main";
        }

    }

    /**
     * 获取登录用户的菜单
     * @return
     */
    @RequestMapping("/findUserMenu")
    @ResponseBody
    public ResponseBo getUserMenu(HttpServletRequest request) {
        Map<String, SysInfo> sysInfoMap=(Map<String, SysInfo>)redisTemplate.opsForValue().get("sysInfoMap");
        SysInfo sysInfo = sysInfoMap.get("system");
        String sysId = sysInfo.getId();
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
        String sysDomain = sysInfo.getDomain();
        result.put("navigatorUrl",sysDomain +"/main");
        result.put("logoutUrl",sysDomain + "/logout");
        result.put("single", user.getUserMenuTree().keySet().size() == 1);
        //获取当前子系统名称
        String sysName=sysInfo.getName();
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
}
