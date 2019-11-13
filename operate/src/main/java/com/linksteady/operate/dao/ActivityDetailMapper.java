package com.linksteady.operate.dao;
import com.linksteady.operate.domain.ActivityDetail;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-08-13
 */
public interface ActivityDetailMapper {

    List<ActivityDetail> getPageList(int start, int end, String headId, Long planDtWid);

    int getDataCount(String headId, Long planDtWid);

    /**
     * 同步推送状态
     */
    void synchPushStatus();

    /**
     * 更新plan表的完成状态
     */
    void updatePlanToFinish();

    /**
     * 更新头表为完成状态(预售)
     */
    void updatePreheatHeaderToDone();

    /**
     * 更新头表为完成状态(正式)
     */
    void updateFormalHeaderToDone();
}
