package com.linksteady.system.controller;

import com.linksteady.common.annotation.Log;
import com.linksteady.common.bo.UserBo;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.*;
import com.linksteady.common.service.CommonFunService;
import com.linksteady.lognotice.service.ExceptionNoticeHandler;
import com.linksteady.common.domain.Menu;
import com.linksteady.system.domain.SysInfo;
import com.linksteady.common.domain.Tree;
import com.linksteady.common.domain.User;
import com.linksteady.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/")
public class PageController extends BaseController {

    @Autowired
    CommonFunService commonFunService;

    @Value("${app.version}")
    private String version;

    @Autowired
    private UserService userService;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @RequestMapping("/index")
        public String index(Model model) {
        // 登录成后，即可通过 Subject 获取登录的用户信息
        UserBo userBo = super.getCurrentUser();
        model.addAttribute("user", userBo);
        model.addAttribute("version", version);
        return "index";
    }

    @RequestMapping("/main")
    public String main(Model model) {
        // 登录成后，即可通过 Subject 获取登录的用户信息
        UserBo userBo = super.getCurrentUser();
        model.addAttribute("user", userBo);
        model.addAttribute("version", version);
        return "main";
    }

    @Log("重置密码")
    @RequestMapping("/resetPass")
    public String resetPass(Model model) {
        // 登录成后，即可通过 Subject 获取登录的用户信息
        UserBo userBo = super.getCurrentUser();
        User user = this.userService.findUserProfile(userBo.getUserId());

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
    public ResponseBo getUserMenu() {

        UserBo userBo = super.getCurrentUser();
        SysInfoBo sysInfoBo=commonFunService.getSysInfoByCode("system");
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
        result.put("navigatorUrl",sysDomain +"/main");
        result.put("logoutUrl",sysDomain + "/logout");
        result.put("single", userBo.getUserMenuTree().keySet().size() == 1);
        //获取当前子系统名称
        try {
            Tree<Menu> tree = userBo.getUserMenuTree().get(sysInfoBo.getSysId());
            result.put("tree", tree);
            return ResponseBo.okWithData(result,sysInfoBo.getSysName());
        } catch (Exception e) {
            log.error("获取用户菜单失败", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e),1,512));
            return ResponseBo.error("获取用户菜单失败！");
        }
    }
}
