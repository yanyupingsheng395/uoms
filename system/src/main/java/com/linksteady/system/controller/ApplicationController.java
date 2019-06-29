package com.linksteady.system.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.linksteady.common.annotation.Log;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.Application;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.SysInfo;
import com.linksteady.lognotice.service.ExceptionNoticeHandler;
import com.linksteady.system.service.ApplicationService;
import com.linksteady.system.service.SystemService;
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

import java.util.List;
import java.util.Map;

/**
 * Created by hxcao on 2019-05-05
 */
@Controller
@Slf4j
public class ApplicationController extends BaseController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @Log("获取应用信息")
    @RequestMapping("application")
    @RequiresPermissions("application:list")
    public String index() {
        return "system/application/application";
    }

    @RequestMapping("application/list")
    @RequiresPermissions("application:list")
    @ResponseBody
    public Map<String, Object> systemList(QueryRequest request, Application application) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<Application> list = this.applicationService.findAllApplication(application);
        PageInfo<Application> pageInfo = new PageInfo<>(list);
        return getDataTable(pageInfo);
    }

    @RequestMapping("application/checkSystemName")
    @ResponseBody
    public boolean checkApplicationName(String name, String oldName) {
        if (StringUtils.isNotBlank(oldName) && name.equalsIgnoreCase(oldName)) {
            return true;
        }
        Application result = this.applicationService.findByName(name);
        return result == null;
    }

    @Log("新增应用")
    @RequiresPermissions("application:add")
    @RequestMapping("application/add")
    @ResponseBody
    public ResponseBo addSystem(Application application) {
        try {
            this.applicationService.addApplication(application);
            return ResponseBo.ok("新增应用成功！");
        } catch (Exception e) {
            log.error("新增应用失败", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e),1,512));
            return ResponseBo.error("新增应用失败，请联系管理员！");
        }
    }

    @RequestMapping("application/getApplication")
    @ResponseBody
    public ResponseBo getApplication(String id) {
        try {
            Application application = this.applicationService.findApplication(id);
            return ResponseBo.ok(application);
        } catch (Exception e) {
            log.error("获取应用信息失败", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e),1,512));
            return ResponseBo.error("获取应用信息失败，请联系管理员！");
        }
    }

    @Log("修改应用")
    @RequiresPermissions("application:update")
    @RequestMapping("application/update")
    @ResponseBody
    public ResponseBo updateApplication(Application application) {
        try {
            this.applicationService.updateApplication(application);
            return ResponseBo.ok("修改应用成功！");
        } catch (Exception e) {
            log.error("修改应用失败", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e),1,512));
            return ResponseBo.error("修改应用失败，请联系管理员！");
        }
    }

    @Log("删除应用")
    @RequiresPermissions("application:delete")
    @RequestMapping("application/delete")
    @ResponseBody
    public ResponseBo deleteApplication(String ids) {
        try {
            this.applicationService.deleteApplication(ids);
            return ResponseBo.ok("删除应用成功！");
        } catch (Exception e) {
            log.error("删除应用失败", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e),1,512));
            return ResponseBo.error("删除应用失败，请联系管理员！");
        }
    }

    @RequestMapping("application/findAllApplication")
    @ResponseBody
    public ResponseBo findAllApplication() {
        List<Application> list = this.applicationService.findAllApplication();
        return ResponseBo.ok(list);
    }

}
