package com.linksteady.operate.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.SysInfo;
import com.linksteady.common.domain.User;
import com.linksteady.common.service.OpenApiService;
import com.linksteady.lognotice.service.ExceptionNoticeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-06-19
 */
@Slf4j
@Controller
@RequestMapping("/api")
public class OpenApiController extends BaseController {

    @Autowired
    private OpenApiService openApiService;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @ResponseBody
    @RequestMapping("/getSysName")
    public String getSysName(@RequestParam("sysId") String sysId) {
        return openApiService.getSysName(sysId);
    }

    @ResponseBody
    @RequestMapping("/updatePassword")
    public ResponseBo updatePassword(@RequestParam("newPassword") String newPassword) {
        try {
            User user = (User) getCurrentUser();
            this.openApiService.updatePassword(user.getUsername(), newPassword);
            getSubject().logout();
            return ResponseBo.ok("更改密码成功！");
        } catch (Exception e) {
            log.error("更改密码失败", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e),1,512));
            return ResponseBo.error("更改密码失败，请联系管理员！");
        }
    }
}
