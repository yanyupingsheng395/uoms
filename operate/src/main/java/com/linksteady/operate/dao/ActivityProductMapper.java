package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivityProduct;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-09-07
 */
public interface ActivityProductMapper {

    int getCount(String headId, String productId, String productName, String productAttr, String stage);

    List<ActivityProduct> getActivityProductListPage(int start, int end, String headId, String productId, String productName, String productAttr, String stage);

    void saveDataList(List<ActivityProduct> productList);

    void insertProductList(String startDate, String endDate, String headId, Long dayPeriod);

    void deleteByHeadId(String headId);

    void saveActivityProduct(ActivityProduct activityProduct);

    ActivityProduct getProductById(String id);

    void updateActivityProduct(ActivityProduct activityProduct);

    void saveActivityProductList(List<ActivityProduct> productList);

    void deleteProduct(@Param("headId") String headId, @Param("stage") String stage, @Param("productList") List<String> productList);

    int validProductNum(String headId, String stage);

    int getSameProductCount(List<ActivityProduct> productList, String headId, String stage);

    void deleteRepeatData(List<ActivityProduct> productList, String headId, String stage);
}
