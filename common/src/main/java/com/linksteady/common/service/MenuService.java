package com.linksteady.common.service;

import com.linksteady.common.domain.Menu;
import com.linksteady.common.domain.Tree;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = "MenuService")
public interface MenuService extends IService<Menu> {

    List<Menu> findUserPermissions(String userName);

    List<Menu> findUserMenus(String userName, String sysId);

    List<Menu> findUserMenus(String userName);

    List<Menu> findAllMenus(Menu menu);

    Tree<Menu> getMenuButtonTree();

    Tree<Menu> getMenuTree(String sysId);

    Tree<Menu> getUserMenu(String userName, String sysId);

    Map<String, Tree<Menu>> getUserMenu(String userName);

    Menu findById(Long menuId);

    Menu findByNameAndType(String menuName, String type);

    void addMenu(Menu menu);

    void updateMenu(Menu menu);

    void deleteMeuns(String menuIds);

    @Cacheable(key = "'url_'+ #p0")
    List<Map<String, String>> getAllUrl(String p1);
}
