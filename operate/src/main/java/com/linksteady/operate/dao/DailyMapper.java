package com.linksteady.operate.dao;

import com.linksteady.operate.domain.DailyInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyMapper {

    List<DailyInfo> getPageList(int start, int end, String touchDt);

    int getTotalCount(@Param("touchDt") String touchDt);

    Map<String, Object> getTipInfo(@Param("headId") String headId);
}
