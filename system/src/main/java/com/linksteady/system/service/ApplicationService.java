package com.linksteady.system.service;

import com.linksteady.common.domain.Application;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
