package com.linksteady.common.service;

import com.linksteady.common.domain.ResponseBo;

import java.util.Map;

/**
 * @author hxcao
 * @date 2019-06-19
 */
public interface OpenApiService {
    /**
     * 获取当前用户的菜单列表
     * @param username
     * @param sysId
     * @return
     */
    Map<String, Object> getUserMenu(String username, String sysId);

    String getSysName(String sysId);
}
