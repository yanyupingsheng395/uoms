package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.ActivityDetailMapper;
import com.linksteady.operate.domain.ActivityDetail;
import com.linksteady.operate.service.ActivityDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @author hxcao
 * @date 2019-08-13
 */
@Service
public class ActivityDetailServiceImpl implements ActivityDetailService {

    @Autowired
    private ActivityDetailMapper activityDetailMapper;

    @Override
    public int getDataCount(int start, int end, String headId, String planDtWid) {
        return activityDetailMapper.getDataCount(headId, Long.valueOf(planDtWid));
    }

    @Override
    public List<ActivityDetail> getPageList(int start, int end, String headId, String planDtWid) {
        return activityDetailMapper.getPageList(start, end, headId, Long.valueOf(planDtWid));
    }
}
