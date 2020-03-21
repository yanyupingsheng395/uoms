package com.linksteady.common.service.impl;

import com.linksteady.common.dao.DictMapper;
import com.linksteady.common.domain.Dict;
import com.linksteady.common.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/3/7
 */
@Service
public class DictServiceImpl implements DictService {

    @Autowired
    private DictMapper dictMapper;

    @Override
    public List<Dict> getDataListByTypeCode(String typeCode) {
        return dictMapper.getDataListByTypeCode(typeCode);
    }
}
