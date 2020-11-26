package com.linksteady.common.service.impl;

import com.linksteady.common.bo.UserBo;
import com.linksteady.common.dao.CommonFunMapper;
import com.linksteady.common.domain.MenuBo;
import com.linksteady.common.domain.SysInfoBo;
import com.linksteady.common.domain.Tree;
import com.linksteady.common.service.CommonFunService;
import com.linksteady.common.util.MD5Utils;
import com.linksteady.common.util.TreeUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    public boolean checkPassword(Long userId, String newPass) {
        return commonFunMapper.checkPassword(userId, newPass)>0;
    }

    @Override
    public List<MenuBo> findUserPermissions(Long userId) {
        return this.commonFunMapper.findUserPermissions(userId);
    }

    @Override
    public List<MenuBo> findUserMenus(Long userId) {
        return this.commonFunMapper.findUserMenusOfAllSys(userId);
    }

    @Override
    public Tree<MenuBo> getUserMenu(Long userId) {
        List<MenuBo> menus = this.findUserMenus(userId);
        List<Tree<MenuBo>> trees = new ArrayList<>();
        menus.forEach(menu -> {
            Tree<MenuBo> tree = new Tree<>();
            tree.setId(menu.getMenuId().toString());
            tree.setParentId(menu.getParentId().toString());
            tree.setText(menu.getMenuName());
            tree.setIcon(menu.getIcon());
            tree.setUrl(menu.getUrl());
            trees.add(tree);
        });
        return TreeUtils.build(trees);
    }
}
