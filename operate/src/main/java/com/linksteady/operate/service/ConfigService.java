package com.linksteady.operate.service;

import com.linksteady.common.domain.Dict;

import java.util.List;
import java.util.Map;

/**
 * Created by hxcao on 2019-04-29
 */
public interface ConfigService {

    /**
     * 根据typeCode加载对应的字典内容
     * @param typeCode
     * @return
     */
    Map<String,String> selectDictByTypeCode(String typeCode);


    /**
     * 更新配置
     * @param name
     * @param value
     */
    void updateConfig(String name,  String value);

    /**
     * 根据name获取value的值
     */
    String getValueByName(String name);

    /**
     * 判断KEY是否存在
     */
    boolean configExists();
}
