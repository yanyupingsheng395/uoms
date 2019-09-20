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

    List<DailyInfo> getTouchPageList(int start, int end, String touchDt);

    int getTotalCount(@Param("touchDt") String touchDt);

    int getTouchTotalCount(@Param("touchDt") String touchDt);

    Map<String, Object> getTipInfo(@Param("headId") String headId);

    void updateStatus(@Param("headId") String headId, @Param("status") String status);

    void updateActualNum(String headId, int num);

    String getStatusById(String headId);

    DailyInfo getKpiVal(String headId);

    String getTouchDt(String headId);

    Map<String, Object> getTotalNum(String headId);

    DailyInfo findById(String headId);
}
