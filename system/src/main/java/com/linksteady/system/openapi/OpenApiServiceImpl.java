package com.linksteady.system.openapi;

import com.google.common.collect.Maps;
import com.linksteady.common.domain.Application;
import com.linksteady.common.domain.Menu;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.Tree;
import com.linksteady.common.service.OpenApiService;
import com.linksteady.system.service.ApplicationService;
import com.linksteady.system.service.MenuService;
import com.linksteady.system.service.SystemService;
import com.linksteady.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
