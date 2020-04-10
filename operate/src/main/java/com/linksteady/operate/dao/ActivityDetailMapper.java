package com.linksteady.operate.dao;
import com.linksteady.operate.domain.ActivityDetail;
import com.linksteady.operate.vo.ActivityContentVO;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-08-13
 */
public interface ActivityDetailMapper {

    List<ActivityDetail> getPageList(int limit, int offset, Long  planId);

    int getDataCount(Long planId);

    /**
     * 查询出所有文案为空的记录的条数
     */
    int selectContentNulls(@Param("planId")  Long planId);

    /**
     * 查询出所有文案长度超长的记录的条数
     */
    int selectContentLimit(@Param("planId")  Long planId,@Param("limit") int limit);

    /**
     * 查询出所有文案含非法变量的记录的条数
     */
    int selectContentVariable(@Param("planId")  Long planId);

    /**
     * 查询出所有推送时间为空的记录的条数
     */
    int selectPushScheduleNulls(@Param("planId")  Long planId);

    /**
     * 查询出所有推送时间格式不正确的记录的条数
     */
    int selectPushScheduleInvalid(@Param("planId")  Long planId);
}
