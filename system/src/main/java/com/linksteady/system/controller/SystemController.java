package com.linksteady.system.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.linksteady.common.annotation.Log;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.Role;
import com.linksteady.common.domain.System;
import com.linksteady.system.service.RoleService;
import com.linksteady.system.service.SystemService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by hxcao on 2019-05-05
 */
@Controller
public class SystemController extends BaseController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SystemService systemService;

    @Log("获取系统信息")
    @RequestMapping("system")
    @RequiresPermissions("system:list")
    public String index() {
        return "system/system/system";
    }

    @RequestMapping("system/list")
    @RequiresPermissions("system:list")
    @ResponseBody
    public Map<String, Object> systemList(QueryRequest request, System system) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<System> list = this.systemService.findAllSystem(system);
        PageInfo<System> pageInfo = new PageInfo<>(list);
        return getDataTable(pageInfo);
    }

    @RequestMapping("system/checkSystemName")
    @ResponseBody
    public boolean checkSystemName(String name, String oldName) {
        if (StringUtils.isNotBlank(oldName) && name.equalsIgnoreCase(oldName)) {
            return true;
        }
        System result = this.systemService.findByName(name);
        return result == null;
    }

    @Log("新增系统")
    @RequiresPermissions("system:add")
    @RequestMapping("system/add")
    @ResponseBody
    public ResponseBo addSystem(System system) {
        try {
            this.systemService.addSystem(system);
            return ResponseBo.ok("新增系统成功！");
        } catch (Exception e) {
            log.error("新增系统失败", e);
            return ResponseBo.error("新增系统失败，请联系网站管理员！");
        }
    }

    @RequestMapping("system/getSystem")
    @ResponseBody
    public ResponseBo getSystem(String id) {
        try {
            System system = this.systemService.findSystem(id);
            return ResponseBo.ok(system);
        } catch (Exception e) {
            log.error("获取系统信息失败", e);
            return ResponseBo.error("获取系统信息失败，请联系网站管理员！");
        }
    }

    @Log("修改系统")
    @RequiresPermissions("system:update")
    @RequestMapping("system/update")
    @ResponseBody
    public ResponseBo updateSystem(System system) {
        try {
            this.systemService.updateSystem(system);
            return ResponseBo.ok("修改系统成功！");
        } catch (Exception e) {
            log.error("修改系统失败", e);
            return ResponseBo.error("修改系统失败，请联系网站管理员！");
        }
    }

    @Log("删除系统")
    @RequiresPermissions("system:delete")
    @RequestMapping("system/delete")
    @ResponseBody
    public ResponseBo deleteSystem(String ids) {
        try {
            this.systemService.deleteSystem(ids);
            return ResponseBo.ok("删除系统成功！");
        } catch (Exception e) {
            log.error("删除系统失败", e);
            return ResponseBo.error("删除系统失败，请联系网站管理员！");
        }
    }

    @RequestMapping("system/findAllSystem")
    @ResponseBody
    public ResponseBo findAllSystem() {
        List<System> list = this.systemService.findAllSystem();
        return ResponseBo.ok(list);
    }

    /**
     * 用户登录成功后获取权限系统
     * @return
     */
    @RequestMapping("system/findUserSystem")
    @ResponseBody
    public ResponseBo findUserSystem() {
        String username = super.getCurrentUser().getUsername();
        List<System> list = this.systemService.findUserSystem(username);
        return ResponseBo.ok(list);
    }
}
