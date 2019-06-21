package com.linksteady.system.service;


import com.linksteady.common.domain.SysInfo;

import java.util.List;

/**
 * Created by hxcao on 2019-05-06
 */
public interface SystemService extends IService<SysInfo> {
    List<SysInfo> findAllSystem(SysInfo system);
    List<SysInfo> findAllSystem();
    SysInfo findByName(String name);
    void addSystem(SysInfo system);
    SysInfo findSystem(String id);
    void updateSystem(SysInfo system);
    void deleteSystem(String ids);
    List<SysInfo> findUserSystem(String username);
}
