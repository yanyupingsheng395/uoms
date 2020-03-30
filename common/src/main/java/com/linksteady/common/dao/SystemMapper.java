package com.linksteady.common.dao;
import com.linksteady.common.config.MyMapper;
import com.linksteady.common.domain.SysInfo;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

/**
 * Created by hxcao on 2019-05-06
 */
public interface SystemMapper extends MyMapper<SysInfo> {

    String getIdFromSeq();

    SysInfo findSystem(@Param("id") String id);

    List<SysInfo> findAll();

    String getNameById(@Param("id") String id);

    /**
     * 获取当前用户具有权限的子系统列表
     * @param username
     * @return
     */
    List<SysInfo> findUserSystem(@Param("username") String username);
}