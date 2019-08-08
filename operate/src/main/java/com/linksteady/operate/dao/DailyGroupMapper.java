package com.linksteady.operate.dao;

import com.linksteady.operate.domain.DailyGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyGroupMapper {

    List<DailyGroup> getDataList(String headId, int start, int end);

    void updateIsChecked(String headId, List<DailyGroup> groupList);

    List<Map<String, Object>> getOriginalGroupCheck();

    int sumCheckedNum(@Param("headId") String headId);

    List<Map<String, Object>> getSelectedGroup(String headId,@Param("activeIdList") List<String> activeIdList, @Param("growthIdList") List<String> growthIdList);

    List<String> getDefaultActive(@Param("headId") String headId);

    List<String> getDefaultGrowth(@Param("headId") String headId);

    void setIsCheckIsTrue(String headId, @Param("groupIdList") List<String> groupIdList);

    void setIsCheckIsFalse(String headId, @Param("groupIdList") List<String> groupIdList);

    int getGroupDataCount(@Param("headId") String headId);

    void setSmsCode(String headId, String groupId, String smsCode);
}
