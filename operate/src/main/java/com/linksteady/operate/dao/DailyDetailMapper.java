package com.linksteady.operate.dao;

import com.linksteady.operate.domain.DailyDetail;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyDetailMapper {

    List<DailyDetail> getPageList(int start, int end, String headId, String userValue, String pathActive);

    int getDataCount(String headId, String userValue, String pathActive);

    List<DailyDetail> getStrategyPageList(int start, int end, String headId);

    int getStrategyCount(String headId);

    List<DailyDetail> getUserEffect(String headId, int start, int end, String whereInfo);

    int getDataListCount(String headId, String whereInfo);

    int findCountByPushStatus(String headId);
}
