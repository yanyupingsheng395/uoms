package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivityWeight;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-08-14
 */
public interface ActivityWeightMapper {

    List<Map<String, Object>> getWeightIdx(String startDt, String endDt);

    LinkedList<ActivityWeight> getTop5weightForBegin(String beginDate);

    LinkedList<ActivityWeight> getTop8weightForBegin(String beginDate);

    ActivityWeight getEffectDateForEnd(String endDate);

}
