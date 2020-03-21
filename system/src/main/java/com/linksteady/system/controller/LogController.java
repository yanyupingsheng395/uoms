package com.linksteady.system.controller;

import java.util.List;
import java.util.Map;

import com.linksteady.common.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.SysLog;

@Controller
@Slf4j
public class LogController extends BaseController {

    @Autowired
    private LogService logService;

    @RequestMapping("log")
    @RequiresPermissions("log:list")
    public String index() {
        return "system/log/log";
    }

    @RequestMapping("log/list")
    @ResponseBody
    public Map<String, Object> logList(QueryRequest request, SysLog log) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<SysLog> list = this.logService.findAllLogs(log);
        PageInfo<SysLog> pageInfo = new PageInfo<>(list);
        return getDataTable(pageInfo);
    }

    @RequiresPermissions("push:delete")
    @RequestMapping("log/delete")
    @ResponseBody
    public ResponseBo deleteLogs(String ids) {
        try {
            this.logService.deleteLogs(ids);
            return ResponseBo.ok("删除日志成功！");
        } catch (Exception e) {
            log.error("删除日志失败", e);
            return ResponseBo.error("删除日志失败，请联系管理员！");
        }
    }


    @RequestMapping("log/test")
    @ResponseBody
    public ResponseBo test() {
        System.out.println(1/0);
        return ResponseBo.ok();
    }
}
