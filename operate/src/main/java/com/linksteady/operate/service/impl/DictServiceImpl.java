package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.DictMapper;
import com.linksteady.operate.domain.Dict;
import com.linksteady.operate.service.DictService;
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
