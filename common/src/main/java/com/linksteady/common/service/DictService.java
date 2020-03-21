package com.linksteady.common.service;

import com.linksteady.common.domain.Dict;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/3/7
 */
public interface DictService {

    List<Dict> getDataListByTypeCode(String typeCode);
}
