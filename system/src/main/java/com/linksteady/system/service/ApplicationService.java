package com.linksteady.system.service;

import com.linksteady.common.domain.Application;
import com.linksteady.common.domain.System;

import java.util.List;

/**
 * Created by hxcao on 2019-05-06
 */
public interface ApplicationService extends IService<Application> {

    List<Application> findAllApplication(Application application);

    List<Application> findAllApplication();

    Application findByName(String name);

    void addApplication(Application application);

    Application findApplication(String id);

    void updateApplication(Application system);

    void deleteApplication(String ids);

}
