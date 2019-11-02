package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.util.DataStatisticsUtils;
import com.linksteady.common.util.DateUtil;
import com.linksteady.operate.dao.ActivityHeadMapper;
import com.linksteady.operate.dao.ActivityProductMapper;
import com.linksteady.operate.dao.ActivityUserMapper;
import com.linksteady.operate.domain.ActivityProduct;
import com.linksteady.operate.domain.ActivityUser;
import com.linksteady.operate.service.ActivityProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2019-09-07
 */
@Slf4j
@Service
public class ActivityProductServiceImpl implements ActivityProductService {

    @Autowired
    private ActivityProductMapper activityProductMapper;

    @Override
    public int getCount(String headId,String productId, String productName, String productAttr, String stage) {
        return activityProductMapper.getCount(headId, productId, productName, productAttr, stage);
    }

    @Override
    public List<ActivityProduct> getActivityProductListPage(int start, int end, String headId,String productId, String productName, String productAttr, String stage) {
        return activityProductMapper.getActivityProductListPage(start, end, headId, productId, productName, productAttr, stage);
    }

    @Override
    public void saveActivityProduct(ActivityProduct activityProduct) {
        activityProductMapper.saveActivityProduct(activityProduct);
    }

    @Override
    public ActivityProduct getProductById(String id) {
        return activityProductMapper.getProductById(id);
    }

    @Override
    public void updateActivityProduct(ActivityProduct activityProduct) {
        activityProductMapper.updateActivityProduct(activityProduct);
    }
}
