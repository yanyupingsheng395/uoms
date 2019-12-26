package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ManualHeader;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019/12/25
 */
public interface ManualHeaderMapper {
    int getHeaderListCount();

    List<ManualHeader> getHeaderListData(int start, int end);

    void saveHeader(ManualHeader manualHeader);

    Map<String, Object> getPushInfo(String headId);

    String getHeadStatus(String headId);

    void updateScheduleDate(ManualHeader manualHeader);

    void updateStatus(String status, String headId);
}
