package com.linksteady.system.service;

import com.linksteady.common.domain.System;

import java.util.List;

/**
 * Created by hxcao on 2019-05-06
 */
public interface SystemService extends IService<System> {
    List<System> findAllSystem(System system);
    List<System> findAllSystem();
    System findByName(String name);
    void addSystem(System system);
    System findSystem(String id);
    void updateSystem(System system);
    void deleteSystem(String ids);
    List<System> findUserSystem(String username);
}
