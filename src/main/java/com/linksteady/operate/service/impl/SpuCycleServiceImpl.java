package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.SpuCycleMapper;
import com.linksteady.operate.domain.SpuCycle;
import com.linksteady.operate.service.SpuCycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by hxcao on 2019-04-29
 */
@Service
public class SpuCycleServiceImpl implements SpuCycleService {

    @Autowired
    private SpuCycleMapper spuCycleMapper;

    @Override
    public List<SpuCycle> getDataList(int startRow, int endRow, String spuId) {
        return spuCycleMapper.getDataList(startRow, endRow, spuId);
    }

    @Override
    public int getTotalCount(String spuId) {
        return spuCycleMapper.getTotalCount(spuId);
    }
}
