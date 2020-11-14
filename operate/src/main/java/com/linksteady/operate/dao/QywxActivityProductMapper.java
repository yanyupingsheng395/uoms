package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivityProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-09-07
 */
public interface QywxActivityProductMapper {

    int getCount(String headId, String productId, String productName, String groupId,String activityType);

    List<ActivityProduct> getActivityProductListPage(int limit, int offset, String headId, String productId, String productName, String groupId,String activityType);

//    void saveDataList(List<ActivityProduct> productList);

    void deleteByHeadId(String headId);

    void saveActivityProduct(ActivityProduct activityProduct);

    ActivityProduct getProductById(String id);

    void updateActivityProduct(ActivityProduct activityProduct);

    void saveActivityProductList(List<ActivityProduct> productList);

    void deleteProduct(@Param("idList") List<String> idList);

    int validProductNum(Long headId, String stage);

    int getSameProductCount(List<String> productIdList, Long headId, String stage);

    void deleteRepeatData(List<ActivityProduct> productList, Long headId, String stage);

    void deleteData(Long headId);

    void deleteData(String headId);

    List<String> getProductIdByHeadId(String headId,String activityType);

    void deleteDataList(@Param("headId") String headId, @Param("productIdList") List<String> productIdList);

    void updateValidInfo(String headId);

    void updateAllValidInfo(String headId);

    int getCountByHeadId(String headId);

    int validProduct(String headId);

    List<String> getGroupIds(Long headId);

    int checkProductId(String headId, String productId);

    List<String> getNotValidProduct(Long headId);

    void updateValidRepeatSkuInfo(String headId);
}
