package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.ActivityDetailMapper;
import com.linksteady.operate.domain.ActivityDetail;
import com.linksteady.operate.service.ActivityDetailService;
import com.linksteady.operate.vo.ActivityContentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author hxcao
 * @date 2019-08-13
 */
@Service
public class ActivityDetailServiceImpl implements ActivityDetailService {

    @Autowired
    private ActivityDetailMapper activityDetailMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;


    @Override
    public int getDataCount(Long planId) {
        return activityDetailMapper.getDataCount(planId);
    }

    @Override
    public List<ActivityDetail> getPageList(int start, int end, Long planId) {
        return activityDetailMapper.getPageList(start, end, planId);
    }

}
