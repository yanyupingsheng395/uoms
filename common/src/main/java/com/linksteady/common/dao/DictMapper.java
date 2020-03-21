package com.linksteady.common.dao;



import com.linksteady.common.domain.Dict;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/3/7
 */
public interface DictMapper {

    List<Dict> getDataListByTypeCode(String typeCode);
}
