package com.linksteady.system.controller;

import com.google.common.collect.Maps;
import com.linksteady.common.config.ShiroProperties;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.LogTypeEnum;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.SysLog;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.service.LogService;
import com.linksteady.system.service.UserService;
import com.linksteady.common.util.HttpContextUtils;
import com.linksteady.common.util.IPUtils;
import com.linksteady.common.util.MD5Utils;
import com.linksteady.system.config.SystemProperties;
import com.linksteady.system.util.code.img.ImageCode;
import com.linksteady.system.util.code.img.ImageCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author
 */
@Controller
@Slf4j
public class LoginController extends BaseController {

    private static final String CODE_KEY = "_code";

    @Autowired
    ShiroProperties shiroProperties;

    @Autowired
    SystemProperties systemProperties;

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private LogService logService;

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

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        /**
         * 如果本地没有cookies缓存则不需要清除
         */
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }
        // 清除session状态
        Enumeration em = request.getSession().getAttributeNames();
        while (em.hasMoreElements()) {
            request.getSession().removeAttribute(em.nextElement().toString());
        }
        //当前系统的名称
        String systemName = configService.getValueByName("system.name");
        model.addAttribute("systemName", systemName);
        return "login";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseBo login(String username, String password, String code) {

        if (!StringUtils.isNotBlank(username)) {
            return ResponseBo.error("用户名不能为空！");
        }
        if (!StringUtils.isNotBlank(password)) {
            return ResponseBo.error("密码不能为空！");
        }

        if (!StringUtils.isNotBlank(code)) {
            return ResponseBo.error("验证码不能为空！");
        }
        long time1=System.currentTimeMillis();

        Session session = super.getSession();
        String sessionCode = (String) session.getAttribute(CODE_KEY);
        if (null != sessionCode && !"".equals(sessionCode) && !code.equalsIgnoreCase(sessionCode)) {
            return ResponseBo.error("验证码错误！");
        }

        long time2=System.currentTimeMillis();
        password = MD5Utils.encrypt(username, password);
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        try {
            Subject subject = getSubject();
            if (subject != null) {
                subject.logout();
            }
            super.login(token);
            this.userService.updateLoginTime(username);
            //记录登录事件
            logLoginEvent(username, "登录成功");

            long time3=System.currentTimeMillis();
            log.info("{}登录总共耗时:{},从进入shiro到结束总共耗时{}",username,time3-time1,time3-time2);
            //判断用户是否首次登陆 如果是强制跳到修改密码界面
            String firstLogin = userService.findByName(username).getFirstLogin();
            if (shiroProperties.isAllowResetPassword() && "Y".equals(firstLogin)) {
                //记录登录事件
                logLoginEvent(username, "登录成功，首次登陆将强制要求修改密码！");
                //首次登陆强制要求修改密码
                return ResponseBo.ok("Y");
            } else {
                //不要求修改密码
                return ResponseBo.ok("N");
            }
        } catch (UnknownAccountException | IncorrectCredentialsException | LockedAccountException e) {
            //记录登录事件
            logLoginEvent(username, "登录失败：未知账号、账号锁定或凭证不正确");
            return ResponseBo.error(e.getMessage());
        } catch (AuthenticationException e) {
            logLoginEvent(username, "登录失败：其它认证失败原因");
            return ResponseBo.error("认证失败！");
        }
    }

    /**
     * 记录登录事件
     *
     * @return
     */
    private void logLoginEvent(String userName, String operation) {
        // 获取request
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        // 设置IP地址
        String ip = IPUtils.getIpAddr(request);
        long time = 0;
        if (systemProperties.isOpenAopLog()) {
            // 保存日志
            SysLog log = new SysLog();
            log.setUsername(userName);
            log.setIp(ip);
            log.setTime(time);
            log.setMethod("com.linksteady.system.controller.LoginController.login()");
            log.setParams(userName);
            log.setLocation("系统管理");
            log.setLogType(LogTypeEnum.PAGE);
            log.setOperation(operation);
            logService.saveLog(log);
        }
    }


    @RequestMapping("/")
    public String redirectIndex() {
        return "redirect:/main";
    }

    @GetMapping("/403")
    public String forbid() {
        return "403";
    }

    @GetMapping(value = "gifCode")
    public void createCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ImageCodeGenerator imageCodeGenerator = new ImageCodeGenerator();
        ImageCode imageCode = imageCodeGenerator.createCode();
        BufferedImage image = imageCode.getImage();
        imageCode.setImage(null);

        Session session = super.getSession();
        session.removeAttribute(CODE_KEY);
        session.setAttribute(CODE_KEY, imageCode.getCode());
        response.setContentType("image/jpeg");
        ImageIO.write(image, "jpeg", response.getOutputStream());
    }

    @RequestMapping("/sysinfo")
    @ResponseBody
    public ResponseBo getSysInfo() {
        Map result = Maps.newHashMap();

        result.put("appname", appname);
        result.put("version", version);
        result.put("appdesc", appdesc);
        result.put("buildtime", buildTime);
        result.put("bootversion", bootversion);
        return ResponseBo.okWithData("", result);
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        Enumeration em = request.getSession().getAttributeNames();
        while (em.hasMoreElements()) {
            request.getSession().removeAttribute(em.nextElement().toString());
        }
        getSubject().logout();
        return "redirect:/login";
    }
}