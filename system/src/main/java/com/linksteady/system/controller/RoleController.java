package com.linksteady.system.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.linksteady.common.annotation.Log;
import com.linksteady.common.bo.UserRoleBo;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.system.domain.Role;
import com.linksteady.common.domain.Tree;
import com.linksteady.system.service.RoleService;
import com.linksteady.system.service.UserRoleService;
import com.linksteady.lognotice.service.ExceptionNoticeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @Log("获取角色信息")
    @RequestMapping("role")
    @RequiresPermissions("role:list")
    public String index() {
        return "system/role/role";
    }

    @RequestMapping("role/list")
    @RequiresPermissions("role:list")
    @ResponseBody
    public Map<String, Object> roleList(QueryRequest request, Role role) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<Role> list = this.roleService.findAllRole(role);
        PageInfo<Role> pageInfo = new PageInfo<>(list);
        return getDataTable(pageInfo);
    }

    /**
     * 获取所有的角色列表（无权限控制)
     * @return
     */
    @RequestMapping("role/list2")
    @ResponseBody
    public ResponseBo roleList2(){
       Map<Long,String> roleMap = this.roleService.findAllRole().stream().collect(Collectors.toMap(Role::getRoleId,Role::getRoleName));
        return ResponseBo.okWithData("",roleMap);
    }

    @RequestMapping("role/getRole")
    @ResponseBody
    public ResponseBo getRole(Long roleId) {
        try {
            Role role = this.roleService.findRoleWithMenus(roleId);
            return ResponseBo.ok(role);
        } catch (Exception e) {
            log.error("获取角色信息失败", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e),1,512));
            return ResponseBo.error("获取角色信息失败，请联系管理员！");
        }
    }

    @RequestMapping("role/checkRoleName")
    @ResponseBody
    public boolean checkRoleName(String roleName, Long roleId) { ;
        //roleId不为空，表示更新
        Role result = this.roleService.findByName(roleName,roleId);

        return result == null;
    }

    @Log("新增角色")
    @RequiresPermissions("role:add")
    @RequestMapping("role/add")
    @ResponseBody
    public ResponseBo addRole(Role role, Long[] menuId) {
        try {
            this.roleService.addRole(role, menuId);
            return ResponseBo.ok("新增角色成功！");
        } catch (Exception e) {
            log.error("新增角色失败", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e),1,512));
            return ResponseBo.error("新增角色失败，请联系管理员！");
        }
    }

    @Log("删除角色")
    @RequiresPermissions("role:delete")
    @RequestMapping("role/delete")
    @ResponseBody
    public ResponseBo deleteRoles(String ids) {
        try {
            this.roleService.deleteRoles(ids);
            return ResponseBo.ok("删除角色成功！");
        } catch (Exception e) {
            log.error("删除角色失败", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e),1,512));
            return ResponseBo.error("删除角色失败，请联系管理员！");
        }
    }

    @Log("修改角色")
    @RequiresPermissions("role:update")
    @RequestMapping("role/update")
    @ResponseBody
    public ResponseBo updateRole(Role role, Long[] menuId) {
        try {
            this.roleService.updateRole(role, menuId);
            return ResponseBo.ok("修改角色成功！");
        } catch (Exception e) {
            log.error("修改角色失败", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e),1,512));
            return ResponseBo.error("修改角色失败，请联系管理员！");
        }
    }

    @RequestMapping("role/getUserRoleTree")
    @ResponseBody
    public ResponseBo getUserRoleTree(@RequestParam("roleId") Long roleId) {
        Tree<UserRoleBo> res = this.roleService.getUserRoleTree(roleId);
        return ResponseBo.okWithData(null, res);
    }

    @RequestMapping("role/updateUserRole")
    @ResponseBody
    public ResponseBo updateUserRole(@RequestParam("userIds") String userIds, @RequestParam("roleId") String roleId) {
        try {
            userRoleService.updateUserRole(userIds, roleId);
            return ResponseBo.ok("授权成功！");
        }catch (Exception e) {
            log.error("授权失败，", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e),1,512));
            return ResponseBo.error("授权失败，未知异常！");
        }
    }
}
