package com.linksteady.common.dao;

/**
 * Created by hxcao on 2019-06-03
 */

import com.linksteady.common.domain.Dict;
import com.linksteady.common.domain.Tconfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通用配置表
 */
public interface ConfigMapper {

    List<Dict> selectDictByTypeCode(String typeCode);

    int updateConfig(@Param("name") String name,@Param("value") String value);

    Tconfig getTconfigByName(String name);

    List<Tconfig> selectConfigList();
}
