package com.linksteady.operate.service;

import com.linksteady.operate.domain.DailyGroup;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyGroupService {

    List<DailyGroup> getDataList(String headId, int start, int end);

    void updateIsChecked(String headId, List<DailyGroup> groupList);

    List<Map<String, Object>> getOriginalGroupCheck();

    List<Map<String, Object>> getSelectedGroup(String headId, String activeIds, String growthIds);

    List<String> getDefaultActive(String headId);

    List<String> getDefaultGrowth(String headId);

    void setGroupCheck(String headId, String groupIds);

    int getGroupDataCount(String headId);

    void setSmsCode(String headId, String groupId, String smsCode);
}
