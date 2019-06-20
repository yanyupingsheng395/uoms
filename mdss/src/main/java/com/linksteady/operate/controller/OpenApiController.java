package com.linksteady.operate.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.User;
import com.linksteady.common.service.OpenApiService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @author hxcao
 * @date 2019-06-19
 */
@Controller
@RequestMapping("/api")
public class OpenApiController {

    @Autowired
    private OpenApiService openApiService;

    @ResponseBody
    @RequestMapping("/getUserMenu")
    public ResponseBo getUserMenu(@RequestParam("sysId") String sysId) {
        String username = ((User)SecurityUtils.getSubject().getPrincipal()).getUsername();
        return openApiService.getUserMenu(username, sysId);
    }
}
