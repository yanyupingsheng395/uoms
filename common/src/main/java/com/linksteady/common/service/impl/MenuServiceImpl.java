package com.linksteady.common.service.impl;

import com.google.common.collect.Maps;
import com.linksteady.common.dao.MenuMapper;
import com.linksteady.common.dao.SystemMapper;
import com.linksteady.common.domain.Menu;
import com.linksteady.common.domain.SysInfo;
import com.linksteady.common.domain.Tree;
import com.linksteady.common.service.MenuService;
import com.linksteady.common.service.RoleMenuServie;
import com.linksteady.common.util.TreeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.*;
import java.util.stream.Collectors;

@Service("menuService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
@Slf4j
public class MenuServiceImpl extends BaseService<Menu> implements MenuService {

    private static final String NODE_PREFIX = "S_";

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private SystemMapper systemMapper;

    @Autowired
    private RoleMenuServie roleMenuService;

    @Autowired
    private WebApplicationContext applicationContext;

    @Override
    public List<Menu> findUserPermissions(String userName) {
        return this.menuMapper.findUserPermissions(userName);
    }

    @Override
    public List<Menu> findUserMenus(String userName, String sysId) {
        return this.menuMapper.findUserMenus(userName, sysId);
    }

    @Override
    public List<Menu> findUserMenus(String userName) {
        return this.menuMapper.findUserMenusOfAllSys(userName);
    }

    @Override
    public List<Menu> findAllMenus(Menu menu) {
        try {
            Example example = new Example(Menu.class);
            Criteria criteria = example.createCriteria();
            if (StringUtils.isNotBlank(menu.getMenuName())) {
                criteria.andCondition("menu_name=", menu.getMenuName());
            }
            if (StringUtils.isNotBlank(menu.getType())) {
                criteria.andCondition("type=", Long.valueOf(menu.getType()));
            }
            if (StringUtils.isNotBlank(menu.getSysId())) {
                criteria.andCondition("sys_id=", Long.valueOf(menu.getSysId()));
            }
            example.setOrderByClause("order_num");
            List<Menu> menuList = this.selectByExample(example);
            for (Menu t: menuList) {
                t.setSysName(systemMapper.getNameById(t.getSysId()));
            }
            return menuList;
        } catch (NumberFormatException e) {
            log.error("error", e);
            return new ArrayList<>();
        }
    }

    @Override
    public Tree<Menu> getMenuButtonTree() {
        List<Tree<Menu>> trees = new ArrayList<>();
        List<Menu> menus = this.findAllMenus(new Menu());
        List<SysInfo> systems = systemMapper.findAll();
        buildTrees(trees, menus, systems);
        return TreeUtils.build(trees);
    }

    @Override
    public Tree<Menu> getMenuTree(String sysId) {
        List<Tree<Menu>> trees = new ArrayList<>();
        Example example = new Example(Menu.class);
        example.createCriteria().andCondition("type =", 0).andCondition("sys_id =", sysId);
        example.setOrderByClause("order_num");
        List<Menu> menus = this.selectByExample(example);
        buildTrees(trees, menus);
        return TreeUtils.build(trees);
    }

    private void buildTrees(List<Tree<Menu>> trees, List<Menu> menus) {
        menus.forEach(menu -> {
            Tree<Menu> tree = new Tree<>();
            tree.setId(menu.getMenuId().toString());
            tree.setParentId(menu.getParentId().toString());
            tree.setText(menu.getMenuName());
            String type = "0";
            if(type.equals(menu.getType())) {
                tree.setIcon("mdi mdi-file-document-box");
            }else {
                tree.setIcon("mdi mdi-fingerprint");
            }
            trees.add(tree);
        });
    }

    private void buildTrees(List<Tree<Menu>> trees, List<Menu> menus, List<SysInfo> systems) {
        menus.forEach(menu -> {
            Tree<Menu> tree = new Tree<>();
            tree.setId(menu.getMenuId().toString());
            tree.setParentId(menu.getParentId().toString().equals("0") ? NODE_PREFIX + menu.getSysId() : menu.getParentId().toString());
            tree.setText(menu.getMenuName());
            String type = "0";
            if(type.equals(menu.getType())) {
                tree.setIcon("mdi mdi-file-document-box");
            }else {
                tree.setIcon("mdi mdi-fingerprint");
            }
            trees.add(tree);
        });
        systems.forEach(system -> {
            Tree<Menu> tree = new Tree<>();
            tree.setId(NODE_PREFIX + system.getId());
            tree.setParentId("0");
            tree.setText(system.getName());
            tree.setIcon("mdi mdi-database");
            trees.add(tree);
        });
    }

    @Override
    public Tree<Menu> getUserMenu(String userName, String sysId) {
        List<Tree<Menu>> trees = new ArrayList<>();
        List<Menu> menus = this.findUserMenus(userName, sysId);
        menus.forEach(menu -> {
            Tree<Menu> tree = new Tree<>();
            tree.setId(menu.getMenuId().toString());
            tree.setParentId(menu.getParentId().toString());
            tree.setText(menu.getMenuName());
            tree.setIcon(menu.getIcon());
            tree.setUrl(menu.getUrl());
            trees.add(tree);
        });
        return TreeUtils.build(trees);
    }

    @Override
    public Map<String, Tree<Menu>> getUserMenu(String userName) {
        List<Menu> menus = this.findUserMenus(userName);
        Map<String, Tree<Menu>> result = Maps.newHashMap();
        menus.stream().collect(Collectors.groupingBy(Menu::getSysId)).entrySet().stream().forEach(x->{
            List<Tree<Menu>> trees = new ArrayList<>();
            x.getValue().forEach(menu -> {
                Tree<Menu> tree = new Tree<>();
                tree.setId(menu.getMenuId().toString());
                tree.setParentId(menu.getParentId().toString());
                tree.setText(menu.getMenuName());
                tree.setIcon(menu.getIcon());
                tree.setUrl(menu.getUrl());
                trees.add(tree);
            });
            result.put(x.getKey(), TreeUtils.build(trees));
        });
        return result;
    }

    @Override
    public Menu findByNameAndType(String menuName, String type) {
        Example example = new Example(Menu.class);
        example.createCriteria().andCondition("lower(menu_name)=", menuName.toLowerCase())
                .andEqualTo("type", Long.valueOf(type));
        List<Menu> list = this.selectByExample(example);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMenu(Menu menu) {
        menu.setMenuId(menuMapper.getId());
        menu.setCreateTime(new Date());
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (Menu.TYPE_BUTTON.equals(menu.getType())) {
            menu.setUrl(null);
            menu.setIcon(null);
        }
        this.save(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMeuns(String menuIds) {
        List<String> list = Arrays.asList(menuIds.split(","));
        this.batchDelete(list, "menuId", Menu.class);
        this.roleMenuService.deleteRoleMenusByMenuId(menuIds);
        this.menuMapper.changeToTop(list);
    }

    @Override
    public List<Map<String, String>> getAllUrl(String p1) {
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        //获取url与类和方法的对应信息
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
        List<Map<String, String>> urlList = new ArrayList<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
            RequestMappingInfo info = entry.getKey();
            HandlerMethod handlerMethod = map.get(info);
            RequiresPermissions permissions = handlerMethod.getMethodAnnotation(RequiresPermissions.class);
            String perms = "";
            if (null != permissions) {
                perms = StringUtils.join(permissions.value(), ",");
            }
            Set<String> patterns = info.getPatternsCondition().getPatterns();
            for (String url : patterns) {
                Map<String, String> urlMap = new HashMap<>();
                urlMap.put("url", url.replaceFirst("\\/", ""));
                urlMap.put("perms", perms);
                urlList.add(urlMap);
            }
        }
        return urlList;

    }

    @Override
    public Menu findById(Long menuId) {
        return this.selectByKey(menuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(Menu menu) {
        menu.setModifyTime(new Date());
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (Menu.TYPE_BUTTON.equals(menu.getType())) {
            menu.setUrl(null);
            menu.setIcon(null);
        }
        this.updateNotNull(menu);
    }

}