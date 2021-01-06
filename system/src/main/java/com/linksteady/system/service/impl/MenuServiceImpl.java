package com.linksteady.system.service.impl;

import com.linksteady.common.bo.UserBo;
import com.linksteady.system.domain.Menu;
import com.linksteady.common.domain.Tree;
import com.linksteady.common.service.impl.BaseService;
import com.linksteady.common.util.TreeUtils;
import com.linksteady.system.dao.MenuMapper;
import com.linksteady.system.dao.SystemMapper;
import com.linksteady.system.domain.SysInfo;
import com.linksteady.system.service.MenuService;
import com.linksteady.system.service.RoleMenuServie;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
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
    public List<Menu> findAllMenus(Menu menu) {
        List<Menu> menuList = menuMapper.findAllMenus(menu);
        return menuList;
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
    public Tree<Menu> getMenuTree(String sysCode) {
        List<Tree<Menu>> trees = new ArrayList<>();
        Example example = new Example(Menu.class);
        example.createCriteria().andCondition("type =", "0").andCondition("sys_code =", sysCode);
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
            tree.setParentId(menu.getParentId().toString().equals("0") ? NODE_PREFIX + menu.getSysCode() : menu.getParentId().toString());
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
            tree.setId(NODE_PREFIX + system.getCode());
            tree.setParentId("0");
            tree.setText(system.getName());
            tree.setIcon("mdi mdi-database");
            trees.add(tree);
        });
    }

    @Override
    public Menu findByNameAndType(String menuName, String type,Long menuId) {
        Example example = new Example(Menu.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andCondition("menu_name=", menuName)
                .andEqualTo("type",type);

        if(null!=menuId)
        {
            criteria.andEqualTo("menuId",menuId);
        }
        List<Menu> list = this.selectByExample(example);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMenu(Menu menu) {
        if(StringUtils.isEmpty(menu.getPerms())) {
            menu.setPerms(null);
        }
        menu.setCreateDt(new Date());
        menu.setCreateBy(((UserBo) SecurityUtils.getSubject().getPrincipal()).getUsername());
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
        List<Long> list = Arrays.asList(menuIds.split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
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
                Map<String, String> urlMap = new HashMap<>(16);
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
        if(StringUtils.isEmpty(menu.getPerms())) {
            menu.setPerms(null);
        }
        menu.setUpdateDt(new Date());
        menu.setUpdateBy(((UserBo) SecurityUtils.getSubject().getPrincipal()).getUsername());
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
