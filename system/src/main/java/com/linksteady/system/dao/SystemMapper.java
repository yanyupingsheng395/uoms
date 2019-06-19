package com.linksteady.system.dao;
import com.linksteady.common.domain.System;
import com.linksteady.system.config.MyMapper;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

/**
 * Created by hxcao on 2019-05-06
 */
public interface SystemMapper extends MyMapper<System> {

    String getIdFromSeq();

    System findSystem(@Param("id") String id);

    List<System> findAll();

    String getNameById(@Param("id") String id);

    /**
     * 获取当前用户具有权限的子系统列表
     * @param username
     * @return
     */
    List<System> findUserSystem(@Param("username") String username);
}
