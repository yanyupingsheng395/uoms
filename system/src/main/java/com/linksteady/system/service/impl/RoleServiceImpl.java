package com.linksteady.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.linksteady.common.bo.UserRoleBo;
import com.linksteady.common.domain.*;
import com.linksteady.lognotice.service.ExceptionNoticeHandler;
import com.linksteady.system.dao.UserRoleMapper;
import com.linksteady.system.util.TreeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.linksteady.system.dao.RoleMapper;
import com.linksteady.system.dao.RoleMenuMapper;
import com.linksteady.system.service.RoleMenuServie;
import com.linksteady.system.service.RoleService;
import com.linksteady.system.service.UserRoleService;
import tk.mybatis.mapper.entity.Example;

@Service("roleService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
@Slf4j
public class RoleServiceImpl extends BaseService<Role> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleMenuServie roleMenuService;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @Override
    public List<Role> findUserRole(String userName) {
        return this.roleMapper.findUserRole(userName);
    }

    @Override
    public List<Role> findAllRole(Role role) {
        try {
            Example example = new Example(Role.class);
            if (StringUtils.isNotBlank(role.getRoleName())) {
                example.createCriteria().andCondition("role_name=", role.getRoleName());
            }
            example.setOrderByClause("create_time");
            return this.selectByExample(example);
        } catch (Exception e) {
            log.error("获取角色信息失败", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e),1,512));
            return new ArrayList<>();
        }
    }

    @Override
    public List<Role> findAllRole() {
        return roleMapper.findAll();
    }


    @Override
    public Role findByName(String roleName) {
        Example example = new Example(Role.class);
        example.createCriteria().andCondition("lower(role_name)=", roleName.toLowerCase());
        List<Role> list = this.selectByExample(example);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    @Transactional
    public void addRole(Role role, Long[] menuIds) {
        role.setCreateTime(new Date());
        this.save(role);
        setRoleMenus(role, menuIds);
    }

    private void setRoleMenus(Role role, Long[] menuIds) {
        Arrays.stream(menuIds).forEach(menuId -> {
            RoleMenu rm = new RoleMenu();
            rm.setMenuId(menuId);
            rm.setRoleId(role.getRoleId());
            this.roleMenuMapper.insert(rm);
        });
    }

    @Override
    @Transactional
    public void deleteRoles(String roleIds) {
        List<String> list = Arrays.asList(roleIds.split(","));
        this.batchDelete(list, "roleId", Role.class);

        this.roleMenuService.deleteRoleMenusByRoleId(roleIds);
        this.userRoleService.deleteUserRolesByRoleId(roleIds);

    }

    @Override
    public RoleWithMenu findRoleWithMenus(Long roleId) {
        List<RoleWithMenu> list = this.roleMapper.findById(roleId);
        List<Long> menuList = new ArrayList<>();
        for (RoleWithMenu rwm : list) {
            menuList.add(rwm.getMenuId());
        }
        if (list.isEmpty()) {
            return null;
        }
        RoleWithMenu roleWithMenu = list.get(0);
        roleWithMenu.setMenuIds(menuList);
        return roleWithMenu;
    }

    @Override
    @Transactional
    public void updateRole(Role role, Long[] menuIds) {
        role.setModifyTime(new Date());
        this.updateNotNull(role);
        Example example = new Example(RoleMenu.class);
        example.createCriteria().andCondition("role_id=", role.getRoleId());
        this.roleMenuMapper.deleteByExample(example);
        setRoleMenus(role, menuIds);
    }

    @Override
    public Tree<UserRoleBo> getUserRoleTree(String roleId) {
        Role role = roleMapper.selectByPrimaryKey(roleId);
        List<Tree<UserRoleBo>> userRoleTree = Lists.newArrayList();
        List<UserRoleBo> userRoleBos = userRoleMapper.findUserRole(roleId);
        userRoleBos.forEach(x->{
            Tree<UserRoleBo> tree = new Tree<>();
            tree.setId(x.getUserId());
            tree.setText(x.getUserName());
            tree.setParentId("-1");
            tree.setIcon("fa fa-user");
            tree.setChecked(x.getHasPermission().equalsIgnoreCase("1")?true:false);
            userRoleTree.add(tree);
        });
        Tree<UserRoleBo> tree = new Tree<>();
        tree.setId("-1");
        tree.setText(role.getRoleName());
        tree.setIcon("fa fa-address-card");
        userRoleTree.add(tree);
        return TreeUtils.build(userRoleTree);
    }
}
