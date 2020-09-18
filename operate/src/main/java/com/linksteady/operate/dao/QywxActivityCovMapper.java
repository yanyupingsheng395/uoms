package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivityCovInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/2/26
 */
public interface QywxActivityCovMapper {

    List<ActivityCovInfo> getCovList(@Param("isDefault") String isDefault);

    List<ActivityCovInfo> getCovInfo(@Param("sql") String sql);

    String getCovId(long headId, String stage);

    ActivityCovInfo getCovInfoById(String covId, String headId, String stage);

    void updatePreheatCovInfo(long headId, String covId, String stage);

    void updateFormalCovInfo(long headId, String covId, String stage);

    List<ActivityCovInfo> getCovListByHeadId(String headId, String stage);

    int checkCovInfo(String headId);

    void deleteData(long headId);
}
