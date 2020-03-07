package com.linksteady.operate.dao;

import com.linksteady.operate.domain.Dict;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/3/7
 */
public interface DictMapper {

    List<Dict> getDataListByTypeCode(String typeCode);
}
