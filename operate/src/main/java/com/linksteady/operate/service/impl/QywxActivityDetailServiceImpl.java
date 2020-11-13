package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.ActivityDetailMapper;
import com.linksteady.operate.dao.QywxActivityDetailMapper;
import com.linksteady.operate.domain.ActivityDetail;
import com.linksteady.operate.domain.QywxActivityDetail;
import com.linksteady.operate.service.ActivityDetailService;
import com.linksteady.operate.service.QywxActivityDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-08-13
 */
@Service
public class QywxActivityDetailServiceImpl implements QywxActivityDetailService {

    @Autowired
    private QywxActivityDetailMapper activityDetailMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;


    @Override
    public int getDataCount(Long planId) {
        return activityDetailMapper.getDataCount(planId);
    }

    @Override
    public List<QywxActivityDetail> getPageList(int limit, int offset, Long planId) {
        return activityDetailMapper.getPageList(limit,offset, planId);
    }

}
