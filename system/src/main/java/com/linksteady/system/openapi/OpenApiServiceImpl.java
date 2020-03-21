package com.linksteady.system.openapi;

import com.linksteady.common.service.OpenApiService;
import com.linksteady.common.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hxcao
 * @date 2019-06-19
 */
@Service
public class OpenApiServiceImpl implements OpenApiService {

    @Autowired
    private UserService userService;

    @Override
    public void updatePassword(String userName, String newPassword) {
        userService.updatePassword(userName, newPassword);
    }
}
