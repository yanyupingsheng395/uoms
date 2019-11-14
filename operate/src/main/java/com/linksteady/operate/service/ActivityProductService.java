package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityProduct;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-09-07
 */
public interface ActivityProductService {

    int getCount(String headId, String productId, String productName, String productAttr, String stage);

    List<ActivityProduct> getActivityProductListPage(int start, int end, String headId, String productId, String productName, String productAttr, String stage);

    /**
     * 保存活动商品
     * @param activityProduct
     */
    void saveActivityProduct(ActivityProduct activityProduct);

    ActivityProduct getProductById(String id);

    void updateActivityProduct(ActivityProduct activityProduct);

    /**
     * 保存活动商品列表
     * @param productList
     */
    void saveActivityProductList(List<ActivityProduct> productList);

    /**
     * 删除活动商品
     * @param headId
     * @param stage
     * @param productIds
     */
    void deleteProduct(String headId, String stage, String productIds);

    int validProductNum(String headId, String stage);

    String generateProductShortUrl(String productId);

    int getSameProductCount(List<String> productIdList, String headId, String stage);

    /**
     * 删除重复的活动商品
     * @param productList
     * @param headId
     * @param stage
     */
    void deleteRepeatData(List<ActivityProduct> productList, String headId, String stage);

    /**
     * 获取第一条商品的信息
     * @return
     */
    ActivityProduct geFirstProductInfo(String headId, String stage);

    /**
     * 更新活动映射信息表
     */
    void insertActivityProdMapping(String headId, String stage);
    /**
     * 删除活动映射信息表
     */
    void deleteActivityProdMapping(String headId, String stage);

    void deleteData(String headId);
}
