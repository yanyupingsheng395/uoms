package com.linksteady.common.dao;

/**
 * Created by hxcao on 2019-06-03
 */

import com.linksteady.common.domain.MenuBo;
import com.linksteady.common.domain.SysInfoBo;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

/**
 * 通用配置表
 */
public interface CommonFunMapper {

    /**
     * 更改用户密码
     * @param userId
     * @param newPassword
     */
    void updatePassword(long userId, String newPassword);

    /**
     * 根据系统的标识符获取当前系统的相关信息
     */
     SysInfoBo getSysInfoByCode(String code);


    int checkPassword(Long userId,String newpass);

    List<MenuBo> findUserPermissions(Long userId);

    List<MenuBo> findUserMenusOfAllSys(@Param("userId") Long userId);
}
