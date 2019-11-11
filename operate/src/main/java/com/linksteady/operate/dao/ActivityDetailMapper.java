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
}
