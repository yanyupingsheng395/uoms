package com.linksteady.operate.service;

import com.linksteady.operate.domain.TargetInfo;
import com.linksteady.operate.vo.TgtReferenceVO;

import java.util.List;
import java.util.Map;

/**
 * Created by hxcao on 2019-05-22
 */
public interface TargetListService {

    long save(TargetInfo target);

    List<Map<String, Object>> getPageList(int startRow, int endRow);

    int getTotalCount();

    Map<String, Object> getDataById(Long id);

    List<TargetInfo> getTargetList(String username);

    Map<String, Object> getMonitorVal(String targetId);

    void deleteDataById(String id);

    Map<String, Object> getDismantData(Long targetId);

    TargetInfo selectByPrimaryKey(long targetId);
}
