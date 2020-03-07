package com.linksteady.operate.service;

import com.linksteady.operate.domain.Dict;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/3/7
 */
public interface DictService {

    List<Dict> getDataListByTypeCode(String typeCode);
}
