package com.linksteady.system.dao;
import com.linksteady.common.config.MyMapper;
import com.linksteady.system.domain.SysInfo;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

/**
 * Created by hxcao on 2019-05-06
 */
public interface SystemMapper extends MyMapper<SysInfo> {

    SysInfo findSystem(@Param("id") Long id);

    List<SysInfo> findAll();
}
