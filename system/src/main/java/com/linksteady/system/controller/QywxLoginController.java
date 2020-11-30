package com.linksteady.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.linksteady.common.constant.CommonConstant;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.SysInfoBo;
import com.linksteady.common.domain.User;
import com.linksteady.common.service.CommonFunService;
import com.linksteady.common.shiro.UoShiroRealm;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.system.exception.QywxLoginException;
import com.linksteady.system.service.QywxLoginService;
import com.linksteady.system.service.UserService;
import com.linksteady.system.shiro.CustomUsernamePasswordToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

/**
 * 企业微信方式进行登录
 */
@Controller
@Slf4j
public class QywxLoginController extends BaseController {

    @Autowired
    QywxLoginService qywxLoginService;

    @Autowired
    CommonFunService commonFunService;

    @Autowired
    UserService userService;

    @Autowired
    UoShiroRealm uoShiroRealm;

    private static final String QW_LOGIN_URL="https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=";

    private static final String GET_ACCESS_TOKEN="/api/getAccessToken";

    public static final String oAuthUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?";

    /**
     * 跳转到扫码授权登录页面
     */
    @GetMapping("/qw/getLoginParam")
    @ResponseBody
    public ResponseBo login(HttpServletRequest request) {
        //获取当前企业的corpid,agentId
        String corpId=qywxLoginService.getCorpId();
        String agentId=qywxLoginService.getAgentId();

        //构造跳转到url
        String basePath =request.getScheme() + "://" +request.getServerName();
        String redirectUrl= null;
        try {
            redirectUrl = java.net.URLEncoder.encode(basePath + "/qw/loginRedirect", "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("构造企业微信扫码登录重定向地址错误");
            return ResponseBo.error("构造企业微信扫码登录重定向地址错误");
        }

        if(StringUtils.isEmpty(corpId)||StringUtils.isEmpty(agentId)||StringUtils.isEmpty(redirectUrl))
        {
            return ResponseBo.error("管理员尚未配置应用，暂时无法支持企业微信登录!");
        }

        Map<String,String> result= Maps.newHashMap();
        result.put("corpId",corpId);
        result.put("agentId",agentId);
        result.put("redirectUrl",redirectUrl);
        String random=String.valueOf(Math.random()*100);
        result.put("random",random);
        //将此随机数放入session
        request.getSession().setAttribute("random",random);

        return ResponseBo.okWithData("",result);
    }

    /**
     * 扫码授权以后重定向的页面
     * @param model
     * @param request
     * @return
     */
    @GetMapping("/qw/loginRedirect")
    public String qwLoginRedirect(Model model, HttpServletRequest request)
    {
        try {
            //授权码
            String code = request.getParameter("code");
            //验证码
            String stage = request.getParameter("state");

            log.info("用户扫码登录，收到的state和code分别为{},{}",stage,code);
            String random=(String) request.getSession().getAttribute("random");
            if(StringUtils.isEmpty(stage)||!stage.equals(random))
            {
                throw new QywxLoginException("非法的请求链接");
            }
            if(StringUtils.isEmpty(code))
            {
                //用户拒绝了请求
                throw new QywxLoginException("用户拒绝了登录请求!");
            }else
            {
                //获取当前应用的accessToken
                SysInfoBo sysInfoBo = commonFunService.getSysInfoByCode(CommonConstant.QYWX_CODE);
                if(null==sysInfoBo|| StringUtils.isEmpty(sysInfoBo.getSysDomain())){
                    throw new QywxLoginException("企业微信应用未配置！");
                }
                log.error("用户通过请求，收到的state和code分别为{},{}",stage,code);

                StringBuffer getAccessTokenUrl=new StringBuffer(sysInfoBo.getSysDomain());
                getAccessTokenUrl.append(GET_ACCESS_TOKEN);
                String accessToken= OkHttpUtil.getRequest(getAccessTokenUrl.toString());
                if(StringUtils.isEmpty(accessToken))
                {
                    throw new QywxLoginException("获取accessToken失败！");
                }

                StringBuffer getUserInfoUrl=new StringBuffer(QW_LOGIN_URL);
                getUserInfoUrl.append(accessToken);
                getUserInfoUrl.append("&code=").append(code);

                String userResult=OkHttpUtil.getRequest(getUserInfoUrl.toString());
                JSONObject object = JSONObject.parseObject(userResult);
                if(null==object||!"0".equals(object.getString("errcode"))){
                    throw new QywxLoginException("微信授权失败！原因为:"+object.getString("errmsg"));
                }else {
                    //获取UserId(企业微信的userId就是t_user表里的username)
                    String username = object.getString("UserId");
                    if (StringUtils.isEmpty(username)) {
                        throw new QywxLoginException("您非当前企业的企业成员!");
                    } else {
                        //判断用户是否已经存在
                        username=username.toLowerCase();
                        User user = userService.findByName(username);

                        //当前用户不存在
                        if (null == user) {
                            //写入用户表
                            User newUser = new User();
                            //备注：用户名需要进行小写处理 因为 用户名:密码登录方式是做了这样处理的
                            newUser.setUsername(username.toLowerCase());
                            newUser.setCreateBy("qywxAutoLogin");
                            newUser.setUpdateBy("qywxAutoLogin");
                            newUser.setCreateDt(new Date());
                            newUser.setUpdateDt(new Date());
                            newUser.setExpireDate(Date.from(LocalDate.now().plusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                            newUser.setUserType("QYWX");
                            userService.save(newUser);
                        }

                        CustomUsernamePasswordToken token = new CustomUsernamePasswordToken(username,"QYWX");
                        try {
                            Subject subject = getSubject();
                            if (subject != null) {
                                uoShiroRealm.clearCache();
                                subject.logout();
                            }
                            super.login(token);
                            uoShiroRealm.execGetAuthorizationInfo();
                            userService.updateLoginTime(username);
                            //记录登录事件
                            userService.logLoginEvent(username, "企业微信登录成功");
                            return "redirect:/index";
                        } catch (Exception e) {
                            log.error("{}使用企业微信扫码登录失败，{}", username, e);
                            userService.logLoginEvent(username, "企业微信登录失败");
                            throw new QywxLoginException("企业微信登录失败，原因为:" + e.getMessage());
                        }
                    }
                }
            }
        } catch (QywxLoginException e) {
            log.error("企业微信登录失败，失败的原因为{}",e);
            model.addAttribute("msg", e.getMessage());
            return "error/qywxLoginError";
        }
    }

    /**
     * 企微oauth登录
     */
    @RequestMapping("/qw/oauth")
    public String login(Model model, HttpServletRequest request) {
        log.info("开始进行登录授权，构造授权url");
        //构造OAuth链接
        StringBuffer sbf = new StringBuffer(oAuthUrl);
        String corpId=qywxLoginService.getCorpId();
        if(StringUtils.isEmpty(corpId))
        {
            model.addAttribute("msg", "管理员尚未完成配置!!");
            return "error/udferror";
        }
        sbf.append("appid=" + corpId);

        String basePath = request.getScheme() + "://" + request.getServerName();
        String redirectUrl = null;
        try {
            redirectUrl = java.net.URLEncoder.encode(basePath + "/qw/oauthRedirect", "utf-8");

            sbf.append("&redirect_uri=" + redirectUrl);
            sbf.append("&response_type=code");
            sbf.append("&scope=snsapi_base");
            sbf.append("&state=linksteady#wechat_redirect");
            return "redirect:" + sbf.toString();
        } catch (UnsupportedEncodingException e) {
            log.error("oauth回调链接加密错误，原因为{}", e);
            model.addAttribute("msg", "引导授权失败，稍后再试!");
            return "error/udfError";
        }
    }

    /**
     * OAuth完成授权的回调请求
     */
    @RequestMapping("/qw/oauthRedirect")
    public String oauthRedirect(Model model, HttpServletRequest request) {
        //授权码
        String code = request.getParameter("code");
        //验证码
        String stage = request.getParameter("state");
        //获取用户的身份信息
        try {
            if (StringUtils.isEmpty(code) || StringUtils.isEmpty(stage)) {
                throw new QywxLoginException("非法请求，参数丢失！");
            }
            if (StringUtils.isEmpty("linksteady") || !stage.equals("linksteady")) {
                throw new QywxLoginException("非法的请求链接！");
            }

            //获取当前应用的accessToken
            SysInfoBo sysInfoBo = commonFunService.getSysInfoByCode(CommonConstant.QYWX_CODE);
            if (null == sysInfoBo || StringUtils.isEmpty(sysInfoBo.getSysDomain())) {
                throw new QywxLoginException("企业微信应用未配置！");
            }

            StringBuffer getAccessTokenUrl = new StringBuffer(sysInfoBo.getSysDomain());
            getAccessTokenUrl.append(GET_ACCESS_TOKEN);
            String accessToken = OkHttpUtil.getRequest(getAccessTokenUrl.toString());
            if (StringUtils.isEmpty(accessToken)) {
                throw new QywxLoginException("获取accessToken失败！");
            }

            StringBuffer getUserInfoUrl = new StringBuffer(QW_LOGIN_URL);
            getUserInfoUrl.append(accessToken);
            getUserInfoUrl.append("&code=").append(code);
            String userResult = OkHttpUtil.getRequest(getUserInfoUrl.toString());
            JSONObject object = JSONObject.parseObject(userResult);
            if (null == object || !"0".equals(object.getString("errcode"))) {
                throw new QywxLoginException("微信授权失败！原因为:" + object.getString("errmsg"));
            } else {
                //获取UserId(企业微信的userId就是t_user表里的username)
                String username = object.getString("UserId");
                if (StringUtils.isEmpty(username)) {
                    throw new QywxLoginException("您非当前企业的企业成员!");
                } else {
                    //判断用户是否已经存在
                    username = username.toLowerCase();
                    User user = userService.findByName(username);

                    //当前用户不存在
                    if (null == user) {
                        //写入用户表
                        User newUser = new User();
                        //备注：用户名需要进行小写处理 因为 用户名:密码登录方式是做了这样处理的
                        newUser.setUsername(username.toLowerCase());
                        newUser.setCreateBy("qywxClientLogin");
                        newUser.setUpdateBy("qywxClientLogin");
                        newUser.setCreateDt(new Date());
                        newUser.setUpdateDt(new Date());
                        newUser.setExpireDate(Date.from(LocalDate.now().plusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        newUser.setUserType("QYWX");
                        userService.save(newUser);
                    }

                    CustomUsernamePasswordToken token = new CustomUsernamePasswordToken(username, "QYWX");
                    Subject subject = getSubject();
                    if (subject != null) {
                        uoShiroRealm.clearCache();
                        subject.logout();
                    }
                    super.login(token);
                    uoShiroRealm.execGetAuthorizationInfo();
                    userService.updateLoginTime(username);
                    //记录登录事件
                    userService.logLoginEvent(username, "企业微信客户端登录成功");
                    log.info("微信回调后sessionid{}",request.getSession().getId());
                   String sourceUrl = (String) getSubject().getSession().getAttribute("sourceUrl");
                    log.info("用户来源的地址为:{}",sourceUrl);
                    if (!StringUtils.isEmpty(sourceUrl)) {
                        String s = sysInfoBo.getSysDomain()+"/qwClient/index";
                        try {
                            s = java.net.URLDecoder.decode(sourceUrl, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        request.getSession().setAttribute("sourceUrl", "");
                        return "redirect:" + sysInfoBo.getSysDomain()+s;
                    } else {
                        return "redirect:"+sysInfoBo.getSysDomain()+"/qwClient/index";
                    }
                }
            }
        }catch (Exception e)
        {
            //未获得授权的请求
            log.error("授权已完成，但是获取用户信息失败，原因为{}", e);
            model.addAttribute("msg", "授权失败!");
            return "error/udferror";
        }
    }

    /**
     * 企业微信授权文件
     */
    @RequestMapping("/{authFileName}.txt")
    @ResponseBody
    public String qywxAuthFile(@PathVariable String authFileName) {
        String oauthFileContent=qywxLoginService.getOauthFileContent();
        return oauthFileContent;
    }

}
