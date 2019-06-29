package com.linksteady.system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.linksteady.lognotice.service.ExceptionNoticeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.linksteady.common.annotation.Log;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.UserOnline;
import com.linksteady.system.service.SessionService;


@Controller
@Slf4j
public class SessionController {

    @Autowired
    SessionService sessionService;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @Log("获取在线用户信息")
    @RequestMapping("session")
    @RequiresPermissions("session:list")
    public String online() {
        return "system/monitor/online";
    }

    @ResponseBody
    @RequestMapping("session/list")
    @RequiresPermissions("session:list")
    public Map<String, Object> list() {
        List<UserOnline> list = sessionService.list();
        Map<String, Object> rspData = new HashMap<>(16);
        rspData.put("rows", list);
        rspData.put("total", list.size());
        return rspData;
    }

    @ResponseBody
    @RequiresPermissions("user:kickout")
    @RequestMapping("session/forceLogout")
    public ResponseBo forceLogout(String id) {
        try {
            sessionService.forceLogout(id);
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("踢出用户失败", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e),1,512));
            return ResponseBo.error("踢出用户失败");
        }

    }
}
