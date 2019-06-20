package com.linksteady.system.service;

import java.util.List;
import java.util.Map;

import com.linksteady.common.domain.Tree;
import com.linksteady.common.domain.Menu;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

@CacheConfig(cacheNames = "MenuService")
public interface MenuService extends IService<Menu> {

    List<Menu> findUserPermissions(String userName);

    List<Menu> findUserMenus(String userName, String sysId);

    List<Menu> findAllMenus(Menu menu);

    Tree<Menu> getMenuButtonTree();

    Tree<Menu> getMenuTree(String sysId);

    Tree<Menu> getUserMenu(String userName, String sysId);

    Menu findById(Long menuId);

    Menu findByNameAndType(String menuName, String type);

    void addMenu(Menu menu);

    void updateMenu(Menu menu);

    void deleteMeuns(String menuIds);

    @Cacheable(key = "'url_'+ #p0")
    List<Map<String, String>> getAllUrl(String p1);
}
