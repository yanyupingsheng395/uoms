package com.linksteady.jobmanager.controller;

import com.linksteady.common.annotation.Log;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author hxcao
 * @date 2019-07-08
 */
@Controller
@RequestMapping("/page")
public class PageController {

    @Log("获取定时任务信息")
    @RequestMapping("/job")
    public String job() {
        return "job/job";
    }

    @Log("获取调度日志信息")
    @RequestMapping("jobLog")
    public String jobLog() {
        return "log/log";
    }
}
