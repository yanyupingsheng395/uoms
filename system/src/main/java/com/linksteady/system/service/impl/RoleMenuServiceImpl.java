package com.linksteady.system.service.impl;

import com.linksteady.system.domain.RoleMenu;
import com.linksteady.common.service.impl.BaseService;
import com.linksteady.system.service.RoleMenuServie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("roleMenuService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class RoleMenuServiceImpl extends BaseService<RoleMenu> implements RoleMenuServie {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteRoleMenusByRoleId(String roleIds) {
		List<Long> list = Arrays.asList(roleIds.split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
		this.batchDelete(list, "roleId", RoleMenu.class);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteRoleMenusByMenuId(String menuIds) {
		List<Long> list = Arrays.asList(menuIds.split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
		this.batchDelete(list, "menuId", RoleMenu.class);
	}

}
