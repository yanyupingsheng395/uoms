package com.linksteady.common.service.impl;

import com.google.common.collect.Maps;
import com.linksteady.common.bo.UserBo;
import com.linksteady.common.dao.CommonFunMapper;
import com.linksteady.common.domain.Menu;
import com.linksteady.common.domain.SysInfoBo;
import com.linksteady.common.domain.Tree;
import com.linksteady.common.service.CommonFunService;
import com.linksteady.common.util.MD5Utils;
import com.linksteady.common.util.TreeUtils;
import org.apache.catalina.security.SecurityUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2019-06-19
 */
@Service
public class CommonFunServiceImpl implements CommonFunService {

    @Autowired
    private CommonFunMapper commonFunMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(long userId, String password) {
        String newPass = MD5Utils.encrypt(((UserBo)SecurityUtils.getSubject().getPrincipal()).getUsername(), password);
        this.commonFunMapper.updatePassword(userId, newPass);
    }

    @Override
    public SysInfoBo getSysInfoByCode(String code) {
        return this.commonFunMapper.getSysInfoByCode(code);
    }

    @Override
    public SysInfoBo getSysInfoById(Long sysId) {
        return this.commonFunMapper.getSysInfoById(sysId);
    }

    @Override
    public boolean checkPassword(Long userId, String newPass) {
        return commonFunMapper.checkPassword(userId, newPass)>0;
    }

    @Override
    public List<Menu> findUserPermissions(Long userId) {
        return this.commonFunMapper.findUserPermissions(userId);
    }

    @Override
    public List<Menu> findUserMenus(Long userId) {
        return this.commonFunMapper.findUserMenusOfAllSys(userId);
    }

    @Override
    public Map<String, Tree<Menu>> getUserMenu(Long userId) {
        List<Menu> menus = this.findUserMenus(userId);
        Map<String, Tree<Menu>> result = Maps.newHashMap();
        menus.stream().collect(Collectors.groupingBy(Menu::getSysCode)).entrySet().stream().forEach(x->{
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
}
