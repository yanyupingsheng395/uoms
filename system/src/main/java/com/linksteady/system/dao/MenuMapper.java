package com.linksteady.system.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.common.domain.Menu;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface MenuMapper extends MyMapper<Menu> {
	
	List<Menu> findUserMenus(@Param("userId") Long userId, @Param("sysCode") String sysCode);

	/**
	 * 	删除父节点，子节点变成顶级节点（根据实际业务调整）
 	 */
	void changeToTop(List<Long> menuIds);

	/**
	 * 查询出所有的菜单
	 */
	List<Menu> findAllMenus(Menu menu);

}