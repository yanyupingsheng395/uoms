package com.linksteady.system.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.linksteady.system.dao.UserRoleMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.linksteady.common.domain.UserRole;
import com.linksteady.system.service.UserRoleService;
import org.springframework.web.bind.annotation.RequestParam;

@Service("userRoleService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserRoleServiceImpl extends BaseService<UserRole> implements UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

	@Override
	@Transactional
	public void deleteUserRolesByRoleId(String roleIds) {
		List<String> list = Arrays.asList(roleIds.split(","));
		this.batchDelete(list, "roleId", UserRole.class);
	}

	@Override
	@Transactional
	public void deleteUserRolesByUserId(String userIds) {
		List<String> list = Arrays.asList(userIds.split(","));
		this.batchDelete(list, "userId", UserRole.class);
	}

    @Override
    @Transactional
	public void updateUserRole(String userIds, String roleId) {
        deleteUserRolesByRoleId(roleId);
        if(StringUtils.isNotBlank(userIds)) {
            List<String> userIdList = Arrays.asList(userIds.split(","));
            List<UserRole> userRoles = userIdList.stream().map(x->{
               UserRole tmp = new UserRole();
               tmp.setRoleId(Long.valueOf(roleId));
               tmp.setUserId(Long.valueOf(x));
               return tmp;
            }).collect(Collectors.toList());
            userRoleMapper.insertDataList(userRoles);
        }
    }
}
