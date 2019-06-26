package com.linksteady.operate.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.SysInfo;
import com.linksteady.common.domain.User;
import com.linksteady.common.service.OpenApiService;
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
@Controller
@RequestMapping("/api")
public class OpenApiController extends BaseController {

    @Autowired
    private OpenApiService openApiService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${app.version}")
    private String version;

    @ResponseBody
    @RequestMapping("/getUserMenu")
    public ResponseBo getUserMenu(HttpServletRequest request) {

        //获取当前用户的sysId
        String sysId = String.valueOf(request.getSession().getAttribute("sysId"));

        if(null==sysId||"".equals(sysId)||"null".equals(sysId))
        {
            return ResponseBo.error("");
        }

        String username = ((User)SecurityUtils.getSubject().getPrincipal()).getUsername();

        Map<String,Object> result=openApiService.getUserMenu(username, sysId);

        User user = super.getCurrentUser();
        String userName = user.getUsername();
        result.put("username", userName);
        result.put("version", version);

        Map<String,String> appMap=(Map<String, String>)redisTemplate.opsForValue().get("applicationInfoMap");
        result.put("navigatorUrl",appMap.get("SYS")+"main");
        result.put("logoutUrl",appMap.get("SYS")+"logout");

        //获取当前子系统名称
        Map<String, SysInfo> sysInfoMap=(Map<String, SysInfo>)redisTemplate.opsForValue().get("sysInfoMap");
        String sysName=null==sysInfoMap.get(sysId)?"":sysInfoMap.get(sysId).getName();

        return ResponseBo.okWithData(result,sysName);
    }

    @ResponseBody
    @RequestMapping("/getSysName")
    public String getSysName(@RequestParam("sysId") String sysId) {
        return openApiService.getSysName(sysId);
    }
}
