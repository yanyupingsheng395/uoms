package com.linksteady.operate.service;

import com.linksteady.operate.domain.SpuCycle;

import java.util.List;

/**
 * Created by hxcao on 2019-04-29
 */
public interface SpuCycleService {
    List<SpuCycle> getDataList(int startRow,int endRow);

    int getTotalCount();
}
