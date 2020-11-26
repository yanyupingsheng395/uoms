package com.linksteady.common.service;

import com.linksteady.common.domain.Menu;
import com.linksteady.common.domain.SysInfoBo;
import com.linksteady.common.domain.Tree;

import java.util.List;
import java.util.Map;

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


    SysInfoBo getSysInfoById(Long sysId);

    boolean checkPassword(Long userId,String newPass);

    List<Menu> findUserPermissions(Long userId);

    List<Menu> findUserMenus(Long userId);

    Map<String, Tree<Menu>> getUserMenu(Long userId);
}
