package com.linksteady.system.controller;
import com.google.common.collect.Maps;
import com.linksteady.common.annotation.Log;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.MD5Utils;
import com.linksteady.system.config.SystemProperties;
import com.linksteady.system.util.code.img.ImageCode;
import com.linksteady.system.util.code.img.ImageCodeGenerator;
import com.linksteady.system.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * @author
 */
@Controller
public class LoginController extends BaseController {

    private static final String CODE_KEY = "_code";

    @Autowired
    SystemProperties systemProperties;

    @Autowired
    private UserService userService;

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
    public String login(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies=request.getCookies();
        /**
         * 如果本地没有cookies缓存则不需要清除
         */
        if(null != cookies) {
            for (Cookie cookie:cookies) {
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }
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
        Session session = super.getSession();
        String sessionCode = (String) session.getAttribute(CODE_KEY);
        if (null!=sessionCode&&!"".equals(sessionCode)&&!code.equalsIgnoreCase(sessionCode)) {
            return ResponseBo.error("验证码错误！");
        }

        password = MD5Utils.encrypt(username.toLowerCase(), password);
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        try {
            Subject subject = getSubject();
            if (subject != null) {
                subject.logout();
            }
            super.login(token);
            this.userService.updateLoginTime(username);

            //判断用户是否首次登陆 如果是强制跳到修改密码界面
            String firstLogin=userService.findByName(username).getFirstLogin();
            if(systemProperties.getShiro().isAllowResetPassword()&&"Y".equals(firstLogin))
            {
                //首次登陆强制要求修改密码
                return ResponseBo.ok("Y");
            }else
            {
                //不要求修改密码
                return ResponseBo.ok("N");
            }


        } catch (UnknownAccountException | IncorrectCredentialsException | LockedAccountException e) {
            return ResponseBo.error(e.getMessage());
        } catch (AuthenticationException e) {
            return ResponseBo.error("认证失败！");
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

        Session session =super.getSession();
        session.removeAttribute(CODE_KEY);
        session.setAttribute(CODE_KEY, imageCode.getCode());
        response.setContentType("image/jpeg");
        ImageIO.write(image, "jpeg", response.getOutputStream());
    }

    @RequestMapping("/sysinfo")
    @ResponseBody
    public ResponseBo getSysInfo() {
        Map result= Maps.newHashMap();

        result.put("appname",appname);
        result.put("version",version);
        result.put("appdesc",appdesc);
        result.put("buildtime",buildTime);
        result.put("bootversion",bootversion);
        return ResponseBo.okWithData("",result);

    }
}