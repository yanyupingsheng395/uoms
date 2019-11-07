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

    @Override
    public void saveActivityProductList(List<ActivityProduct> productList) {
        activityProductMapper.saveActivityProductList(productList);
    }

    /**
     * todo 更新状态
     * 删除商品，删除完更新head表的数据状态
     * @param headId
     * @param stage
     * @param productIds
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(String headId, String stage, String productIds) {
        List<String> productList = Arrays.asList(productIds.split(","));
        activityProductMapper.deleteProduct(headId, stage, productList);
    }

    @Override
    public int validProductNum(String headId, String stage) {
        return activityProductMapper.validProductNum(headId, stage);
    }

    @Override
    public int getSameProductCount(List<ActivityProduct> productList, String headId, String stage) {
        return activityProductMapper.getSameProductCount(productList, headId, stage);
    }
}
