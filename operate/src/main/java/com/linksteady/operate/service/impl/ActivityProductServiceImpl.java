package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.ActivityHeadMapper;
import com.linksteady.operate.dao.ActivityProductMapper;
import com.linksteady.operate.domain.ActivityProduct;
import com.linksteady.operate.service.ActivityProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * @author hxcao
 * @date 2019-09-07
 */
@Slf4j
@Service
public class ActivityProductServiceImpl implements ActivityProductService {

    @Autowired
    private ShortUrlServiceImpl shortUrlService;

    @Autowired
    private ActivityProductMapper activityProductMapper;

    @Autowired
    private ActivityHeadMapper activityHeadMapper;

    @Override
    public int getCount(String headId,String productId, String productName, String productAttr, String stage) {
        return activityProductMapper.getCount(headId, productId, productName, productAttr, stage);
    }

    @Override
    public List<ActivityProduct> getActivityProductListPage(int start, int end, String headId,String productId, String productName, String productAttr, String stage) {
        return activityProductMapper.getActivityProductListPage(start, end, headId, productId, productName, productAttr, stage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveActivityProduct(ActivityProduct activityProduct) {
        String time = String.valueOf(System.currentTimeMillis());
        // 添加商品更改数据状态
        activityHeadMapper.updateGroupChanged(time, activityProduct.getHeadId().toString(), activityProduct.getActivityStage(), "1");
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
    @Transactional(rollbackFor = Exception.class)
    public void saveActivityProductList(List<ActivityProduct> productList) {
        ActivityProduct activityProduct = productList.get(0);
        String time = String.valueOf(System.currentTimeMillis());
        activityHeadMapper.updateGroupChanged(time, activityProduct.getHeadId().toString(), activityProduct.getActivityStage(), "1");
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
        String time = String.valueOf(System.currentTimeMillis());
        activityHeadMapper.updateGroupChanged(time, headId, stage, "1");
        activityProductMapper.deleteProduct(headId, stage, productList);
    }

    @Override
    public int validProductNum(String headId, String stage) {
        return activityProductMapper.validProductNum(headId, stage);
    }

    /**
     * 根据传入的productId 返回去对应的商品明细页短链接
     * @param productId
     * @return
     */
    @Override
    public String generateProductShortUrl(String productId) {
        return "t.cn/"+productId;
    }

    @Override
    public int getSameProductCount(List<ActivityProduct> productList, String headId, String stage) {
        return activityProductMapper.getSameProductCount(productList, headId, stage);
    }
}
