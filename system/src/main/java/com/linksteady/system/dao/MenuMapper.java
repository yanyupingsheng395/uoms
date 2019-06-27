package com.linksteady.system.dao;

import java.util.List;

import com.linksteady.system.config.MyMapper;
import com.linksteady.common.domain.Menu;
import io.lettuce.core.dynamic.annotation.Param;

public interface MenuMapper extends MyMapper<Menu> {
	
	List<Menu> findUserPermissions(String userName);
	
	List<Menu> findUserMenus(@Param("userName") String userName, @Param("sysId") String sysId);

	List<Menu> findUserMenusOfAllSys(@Param("userName") String userName);

	/**
	 * 	删除父节点，子节点变成顶级节点（根据实际业务调整）
 	 */
	void changeToTop(List<String> menuIds);

	/**
	 * 获取t_menu_id sequence的ID
	 */
	Long getId();
}