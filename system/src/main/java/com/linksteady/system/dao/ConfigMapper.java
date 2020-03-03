package com.linksteady.system.dao;

/**
 * Created by admin
 */

import com.linksteady.common.domain.Tconfig;

import java.util.List;

public interface ConfigMapper {
    /**
     * 加载其它通用配置
     */
    List<Tconfig> selectCommonConfig();

}
