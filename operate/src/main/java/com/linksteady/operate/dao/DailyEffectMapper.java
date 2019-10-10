package com.linksteady.operate.dao;

import com.linksteady.operate.domain.DailyEffect;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-08-01
 */
public interface DailyEffectMapper {

    List<DailyEffect> getPageList(int start, int end, String touchDt);

    DailyEffect getKpiStatis(String headId);

    /**
     * 更新统计表中的触达统计信息
     */
    void updatePushStatInfo();

    /**
     * 更新转化数据
     */
    void updateConversion();
}
