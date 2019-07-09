package com.linksteady.common.service;

import com.linksteady.common.domain.ResponseBo;

import java.util.Map;

/**
 * @author hxcao
 * @date 2019-06-19
 */
public interface OpenApiService {
    /**
     * 更改用户密码
     * @param userName
     * @param newPassword
     */
    void updatePassword(String userName, String newPassword);
}
