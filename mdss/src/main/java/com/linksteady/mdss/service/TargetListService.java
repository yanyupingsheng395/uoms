package com.linksteady.mdss.service;

import com.linksteady.mdss.domain.TargetInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by hxcao on 2019-05-22
 */
public interface TargetListService {

    long save(TargetInfo target);

    List<Map<String, Object>> getPageList(int startRow, int endRow, String targetName);

    int getTotalCount(String targetName);

    Map<String, Object> getDataById(Long id);

    List<TargetInfo> getTargetList(String username);

    Map<String, Object> getMonitorVal(String targetId);

    void deleteDataById(String id);

    Map<String, Object> getDismantData(Long targetId);

    TargetInfo selectByPrimaryKey(long targetId);

    void updateTargetStatus(long targetId,String status);
}
