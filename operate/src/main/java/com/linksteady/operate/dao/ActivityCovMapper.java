package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivityCovInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/2/26
 */
public interface ActivityCovMapper {

    List<ActivityCovInfo> getCovList(@Param("isDefault") String isDefault);

    void insertCovInfo(String headId, String covListId, String stage);

    List<ActivityCovInfo> getCovInfo(@Param("sql") String sql);

    String getCovId(String headId, String stage);

    ActivityCovInfo getCovInfoById(String covId);

    void updatePreheatCovInfo(String headId, String covId);

    void updateFormalCovInfo(String headId, String covId);
}
