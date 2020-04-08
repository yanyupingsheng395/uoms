package com.linksteady.system.service.impl;

import com.linksteady.common.bo.UserBo;
import com.linksteady.system.dao.UserRoleMapper;
import com.linksteady.system.domain.UserRole;
import com.linksteady.common.service.impl.BaseService;
import com.linksteady.system.service.UserRoleService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service("userRoleService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserRoleServiceImpl extends BaseService<UserRole> implements UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteUserRolesByRoleId(String roleIds) {
        List<Long> list = Arrays.asList(roleIds.split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
		this.batchDelete(list, "roleId", UserRole.class);
	}

	@Override
    @Transactional(rollbackFor = Exception.class)
	public void deleteUserRolesByUserId(String userIds) {
        List<Long> list = Arrays.asList(userIds.split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
		this.batchDelete(list, "userId", UserRole.class);
	}

    @Override
    @Transactional(rollbackFor = Exception.class)
	public void updateUserRole(String userIds, String roleId) {
        deleteUserRolesByRoleId(roleId);
        if(StringUtils.isNotBlank(userIds)) {
            List<Long> list = Arrays.asList(userIds.split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
            List<UserRole> userRoles = list.stream().map(x->{
               UserRole tmp = new UserRole();
               tmp.setRoleId(Long.valueOf(roleId));
               tmp.setUserId(x);
               tmp.setCreateDt(new Date());
               tmp.setCreateBy(((UserBo) SecurityUtils.getSubject().getPrincipal()).getUsername());
               return tmp;
            }).collect(Collectors.toList());
            userRoleMapper.insertDataList(userRoles);
        }
    }
}
