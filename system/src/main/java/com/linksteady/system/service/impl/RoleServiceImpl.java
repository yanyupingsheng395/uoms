package com.linksteady.system.service.impl;

import com.google.common.collect.Lists;
import com.linksteady.common.bo.UserBo;
import com.linksteady.common.bo.UserRoleBo;
import com.linksteady.smp.starter.lognotice.service.ExceptionNoticeHandler;
import com.linksteady.system.dao.RoleMapper;
import com.linksteady.system.dao.RoleMenuMapper;
import com.linksteady.system.dao.UserRoleMapper;
import com.linksteady.system.domain.Role;
import com.linksteady.system.domain.RoleMenu;
import com.linksteady.system.domain.RoleWithMenu;
import com.linksteady.common.domain.Tree;
import com.linksteady.common.service.impl.BaseService;
import com.linksteady.common.util.TreeUtils;
import com.linksteady.system.service.RoleMenuServie;
import com.linksteady.system.service.RoleService;
import com.linksteady.system.service.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<Role> findUserRole(long userId) {
        return this.roleMapper.findUserRole(userId);
    }

    @Override
    public List<Role> findAllRole(Role role) {
        try {
            Example example = new Example(Role.class);
            if (StringUtils.isNotBlank(role.getRoleName())) {
                example.createCriteria().andLike("roleName","%"+ role.getRoleName() +"%");
            }
            example.setOrderByClause("create_dt");
            return this.selectByExample(example);
        } catch (Exception e) {
            log.error("获取角色信息失败", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Role> findAllRole() {
        return roleMapper.findAll();
    }


    @Override
    public Role findByName(String roleName,Long roleId) {
        Example example = new Example(Role.class);
        Example.Criteria criteria=example.createCriteria();

        criteria.andLike("roleName", "%"+ roleName +"%");

        if(null!=roleId)
        {
            criteria.andNotEqualTo("roleId",roleId);
        }
        List<Role> list = this.selectByExample(example);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRole(Role role, Long[] menuIds) {
        role.setCreateDt(new Date());
        role.setCreateBy(((UserBo) SecurityUtils.getSubject().getPrincipal()).getUsername());
        roleMapper.saveRole(role);
        setRoleMenus(role, menuIds);
    }

    private void setRoleMenus(Role role, Long[] menuIds) {
        Arrays.stream(menuIds).forEach(menuId -> {
            RoleMenu rm = new RoleMenu();
            rm.setMenuId(menuId);
            rm.setRoleId(role.getRoleId());
            rm.setCreateDt(new Date());
            rm.setCreateBy(((UserBo) SecurityUtils.getSubject().getPrincipal()).getUsername());
            this.roleMenuMapper.insert(rm);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoles(String roleIds) {
        List<Long> list = Arrays.asList(roleIds.split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
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
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Role role, Long[] menuIds) {
        role.setUpdateDt(new Date());
        role.setUpdateBy(((UserBo) SecurityUtils.getSubject().getPrincipal()).getUsername());
        this.updateNotNull(role);
        Example example = new Example(RoleMenu.class);
        example.createCriteria().andCondition("role_id=", role.getRoleId());
        this.roleMenuMapper.deleteByExample(example);
        setRoleMenus(role, menuIds);
    }

    @Override
    public Tree<UserRoleBo> getUserRoleTree(Long roleId) {
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
