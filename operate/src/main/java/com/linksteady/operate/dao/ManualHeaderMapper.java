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

    Map<String, Object> getPushInfo(Long headId);

    String getHeadStatus(Long headId);

    void updateScheduleDate(ManualHeader manualHeader);

    /**
     * 更新当前推送计划为 已提交，计划中
     * @param headId
     * @return
     */
    int updateStatusToPlaning(Long headId);

    void deleteData(Long headId);
}
