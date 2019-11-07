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

    void saveActivityProduct(ActivityProduct activityProduct);

    ActivityProduct getProductById(String id);

    void updateActivityProduct(ActivityProduct activityProduct);

    void saveActivityProductList(List<ActivityProduct> productList);

    void deleteProduct(String headId, String stage, String productIds);
}
