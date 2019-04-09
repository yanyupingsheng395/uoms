package com.linksteady.operate.service.impl;

import com.google.common.collect.Maps;
import com.linksteady.common.util.DateUtil;
import com.linksteady.operate.dao.LifeCycleMapper;
import com.linksteady.operate.dao.ReasonMapper;
import com.linksteady.operate.domain.Reason;
import com.linksteady.operate.domain.ReasonKpis;
import com.linksteady.operate.service.LifeCycleService;
import com.linksteady.operate.service.ReasonService;
import com.linksteady.operate.vo.ReasonVO;
import org.assertj.core.util.Lists;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class LifeCycleServiceImpl implements LifeCycleService {

    @Autowired
    private LifeCycleMapper lifeCycleMapper;


    @Override
    public List<Map<String, Object>> getCatList(int startRow, int endRow,String orderColumn,String cateName) {
        return lifeCycleMapper.getCatList(startRow, endRow,orderColumn,cateName);
    }

    @Override
    public int getCatTotalCount(String cateName) {
        return lifeCycleMapper.getCatTotalCount(cateName);
    }
}
