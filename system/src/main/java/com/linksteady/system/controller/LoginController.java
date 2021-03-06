package com.linksteady.system.controller;

import com.linksteady.common.config.ShiroProperties;
import com.linksteady.common.constant.CommonConstant;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.SysInfoBo;
import com.linksteady.common.service.CommonFunService;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.shiro.UoShiroRealm;
import com.linksteady.system.service.UserService;
import com.linksteady.system.shiro.CustomUsernamePasswordToken;
import com.linksteady.system.util.code.img.ImageCode;
import com.linksteady.system.util.code.img.ImageCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
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
    private UserService userService;

    @Autowired
    private ConfigService configService;

    @Autowired
    UoShiroRealm uoShiroRealm;

    @Autowired
    CommonFunService commonFunService;

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        /**
         * ??????????????????cookies????????????????????????
         */
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }
        // ??????session??????
        Enumeration em = request.getSession().getAttributeNames();
        while (em.hasMoreElements()) {
            request.getSession().removeAttribute(em.nextElement().toString());
        }
        //?????????????????????
        String systemName = configService.getValueByName("system.name");
        model.addAttribute("systemName", systemName);
        return "login";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseBo login(String username, String password, String code) {

        if (!StringUtils.isNotBlank(username)) {
            return ResponseBo.error("????????????????????????");
        }
        if (!StringUtils.isNotBlank(password)) {
            return ResponseBo.error("?????????????????????");
        }

        if (!StringUtils.isNotBlank(code)) {
            return ResponseBo.error("????????????????????????");
        }
        long time1=System.currentTimeMillis();

        Session session = super.getSession();
        String sessionCode = (String) session.getAttribute(CODE_KEY);
        if (null != sessionCode && !"".equals(sessionCode) && !code.equalsIgnoreCase(sessionCode)) {
            return ResponseBo.error("??????????????????");
        }

        long time2=System.currentTimeMillis();
        CustomUsernamePasswordToken token = new CustomUsernamePasswordToken(username, password,"PASS");
        try {
            Subject subject = getSubject();
            if (subject != null) {
                uoShiroRealm.clearCache();
                subject.logout();
            }
            super.login(token);
            uoShiroRealm.execGetAuthorizationInfo();
            this.userService.updateLoginTime(username);
            //??????????????????
            userService.logLoginEvent(username, "????????????");

            long time3=System.currentTimeMillis();
            log.info("{}??????????????????:{},?????????shiro?????????????????????{}",username,time3-time1,time3-time2);
            //?????????????????????????????? ???????????????????????????????????????
            String firstLogin = userService.findByName(username).getFirstLogin();
            if (shiroProperties.isAllowResetPassword() && "Y".equals(firstLogin)) {
                //??????????????????
                userService.logLoginEvent(username, "?????????????????????????????????????????????????????????");
                //????????????????????????????????????
                return ResponseBo.ok("Y");
            } else {
                //?????????????????????
                return ResponseBo.ok("N");
            }
        } catch (UnknownAccountException | IncorrectCredentialsException | LockedAccountException e) {
            //??????????????????
            userService.logLoginEvent(username, "????????????????????????????????????????????????????????????");
            return ResponseBo.error(e.getMessage());
        } catch (AuthenticationException e) {
            userService.logLoginEvent(username, "???????????????????????????????????????");
            return ResponseBo.error("???????????????");
        }
    }


    @RequestMapping("/")
    public String redirectIndex() {
        return "redirect:/index";
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

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        Enumeration em = request.getSession().getAttributeNames();
        while (em.hasMoreElements()) {
            request.getSession().removeAttribute(em.nextElement().toString());
        }
        getSubject().logout();
        //??????????????????
        SysInfoBo system=commonFunService.getSysInfoByCode(CommonConstant.SYS_CODE);

        return "redirect:"+system.getSysDomain()+"/login";
    }
}