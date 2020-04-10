package com.linksteady.system.service;


import com.linksteady.system.domain.SysInfo;
import com.linksteady.common.service.IService;

import java.util.List;

/**
 * Created by hxcao on 2019-05-06
 */
public interface SystemService extends IService<SysInfo> {
    List<SysInfo> findAllSystem(SysInfo system);
    List<SysInfo> findAllSystem();
    SysInfo findByName(String name,Long id);
    void addSystem(SysInfo system);
    SysInfo findSystem(Long id);
    void updateSystem(SysInfo system);
    void deleteSystem(String ids);
    List<SysInfo> findUserSystem(Long userId);
}
