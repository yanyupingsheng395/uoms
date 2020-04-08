package com.linksteady.system.service;

import com.linksteady.common.domain.Menu;
import com.linksteady.common.domain.Tree;
import com.linksteady.common.service.IService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = "MenuService")
public interface MenuService extends IService<Menu> {

    List<Menu> findUserPermissions(Long userId);

    List<Menu> findUserMenus(Long userId, String sysCode);

    List<Menu> findUserMenus(Long userId);

    List<Menu> findAllMenus(Menu menu);

    Tree<Menu> getMenuButtonTree();

    Tree<Menu> getMenuTree(String sysCode);

//    Tree<Menu> getUserMenu(Long userId, String sysCode);

    Map<String, Tree<Menu>> getUserMenu(Long userId);

    Menu findById(Long menuId);

    Menu findByNameAndType(String menuName, String type);

    void addMenu(Menu menu);

    void updateMenu(Menu menu);

    void deleteMeuns(String menuIds);

    @Cacheable(key = "'url_'+ #p0")
    List<Map<String, String>> getAllUrl(String p1);
}
