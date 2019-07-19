package com.linksteady.mdss.dao;

import com.linksteady.mdss.config.MyMapper;
import com.linksteady.mdss.domain.TargetInfo;
import com.linksteady.mdss.domain.TgtReference;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

public interface TargetListMapper extends MyMapper<TargetInfo> {
    Long save(@Param("target") TargetInfo target);

    List<Map<String, Object>> getPageList(@Param("startRow") int startRow, @Param("endRow") int endRow, @Param("userId") String userId, @Param("tgtName") String tgtName);

    int getTotalCount();

    int getTotalCountByUsername(@Param("username") String username, @Param("targetName") String targetName);

    Map<String, Object> getDataById(Long id);

    List<TgtReference> getGmvHistoryByPeroid(@Param("sql") String sql);

    TgtReference getGmvHistoryByPeriodDay(@Param("sql") String sql);

    List<TargetInfo> getTargetList(@Param("username") String username);

    Map<String, Object> getMonitorVal(String targetId);

    void updateTargetStatus(@Param("id") long id,@Param("status") String status);

    void updateTargetActualValue(TargetInfo targetInfo);

    void updateFinshDiff(@Param("targetId") long targetId);

    void updateFinshDiffWithZero(@Param("targetId") long targetId);

    /**
     * 获取所有处于执行中的任务列表
     * @return
     */
    List<TargetInfo> getAllRuningTarget();

    void deleteTgtListById(@Param("ids") List<String> ids);
}