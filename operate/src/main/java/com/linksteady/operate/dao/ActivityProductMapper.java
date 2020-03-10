package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivityProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * @author hxcao
 * @date 2019-09-07
 */
public interface ActivityProductMapper {

    int getCount(String headId, String productId, String productName, String groupId);

    List<ActivityProduct> getActivityProductListPage(int start, int end, String headId, String productId, String productName, String groupId);

    void saveDataList(List<ActivityProduct> productList);

    void deleteByHeadId(String headId);

    void saveActivityProduct(ActivityProduct activityProduct);

    ActivityProduct getProductById(String id);

    void updateActivityProduct(ActivityProduct activityProduct);

    void saveActivityProductList(List<ActivityProduct> productList);

    void deleteProduct(@Param("headId") Long headId, @Param("stage") String stage, @Param("productList") List<String> productList);

    int validProductNum(Long headId, String stage);

    int getSameProductCount(List<String> productIdList, Long headId, String stage);

    void deleteRepeatData(List<ActivityProduct> productList, Long headId, String stage);

    void deleteData(Long headId);
    ActivityProduct geFirstProductInfo(Long headId, String stage);

    void deleteData(String headId);

    List<String> getProductIdByHeadId(String headId);

    void deleteDataList(@Param("headId") String headId, @Param("productIdList") List<String> productIdList);

    void updateValidInfo(String headId);

    void updateAllValidInfo(String headId);

    int getCountByHeadId(String headId);

    int validProduct(String headId);
}
