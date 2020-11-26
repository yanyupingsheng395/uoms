package com.linksteady.common.service;

import com.linksteady.common.domain.MenuBo;
import com.linksteady.common.domain.SysInfoBo;
import com.linksteady.common.domain.Tree;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-06-19
 */
public interface CommonFunService {
    /**
     * 更改用户密码
     * @param userId
     * @param newPassword
     */
    void updatePassword(long userId, String newPassword);

    /**
     * 根据系统名称获取系统的相关信息
     * @return
     */
    SysInfoBo getSysInfoByCode(String code);

    boolean checkPassword(Long userId,String newPass);

    List<MenuBo> findUserPermissions(Long userId);

    List<MenuBo> findUserMenus(Long userId);

    Tree<MenuBo> getUserMenu(Long userId);
}
