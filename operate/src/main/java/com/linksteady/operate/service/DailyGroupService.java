package com.linksteady.operate.service;

import com.linksteady.operate.domain.DailyGroup;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyGroupService {

    List<DailyGroup> getDataList(String headId);

    void updateIsChecked(String headId, List<DailyGroup> groupList);
}
