package com.linksteady.operate.dao;
import com.linksteady.operate.domain.ActivityDetail;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-08-13
 */
public interface QywxActivityDetailMapper {

    int getDataCount(Long planId);

    List<ActivityDetail> getPageList(int limit, int offset, Long planId);

    void deleteData(Long headId);

    /**
     * 查询出所有文案为空的记录的条数
     */
    int selectContentNulls(@Param("planId") Long planId);
}
