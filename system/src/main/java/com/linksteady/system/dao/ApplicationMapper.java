package com.linksteady.system.dao;
import com.linksteady.common.domain.Application;
import com.linksteady.common.domain.System;
import com.linksteady.system.config.MyMapper;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

/**
 * Created by hxcao on 2019-05-06
 */
public interface ApplicationMapper extends MyMapper<Application> {

    String getIdFromSeq();

    Application findApplication(@Param("id") String id);

    String getDomainById(String id);

    List<Application> findAll();

    String getNameById(@Param("id") String id);

}
