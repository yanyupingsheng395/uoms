package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ManualHeader;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019/12/25
 */
public interface ManualHeaderMapper {
    int getHeaderListCount(@Param("scheduleDate") String scheduleDate);

    List<ManualHeader> getHeaderListData(int limit, int offset, String scheduleDate);

    void saveHeader(ManualHeader manualHeader);

    Map<String, Object> getPushInfo(String headId);

    String getHeadStatus(String headId);

    void updateScheduleDate(ManualHeader manualHeader);

    void updateStatus(String status, String headId);

    void deleteData(String headId);
}
