package com.linksteady.operate.service;

import com.linksteady.operate.domain.DailyDetail;
import com.linksteady.operate.vo.Echart;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyDetailService {

    Echart getTargetType(String headId);

    Echart getUrgency(String headId);

    List<DailyDetail> getPageList(int start, int end, String headId, String groupId);

    int getDataCount(String headId, String groupId);
}
