package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityProduct;
import com.linksteady.operate.domain.ActivityProductUploadError;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-09-07
 */
public interface QywxActivityProductService {

    int getCount(String headId, String productId, String productName, String groupId);

    List<ActivityProduct> getActivityProductListPage(int limit, int offset, String headId, String productId, String productName, String groupId);

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
     */
    void deleteProduct(String ids);

    int validProductNum(Long headId, String stage);

    String generateProductShortUrl(String productId, String sourceType);

    int getSameProductCount(List<String> productIdList, Long headId, String stage);

    /**
     * 删除重复的活动商品
     * @param productList
     * @param headId
     * @param stage
     */
    void deleteRepeatData(List<ActivityProduct> productList, Long headId, String stage);

    /**
     * 上传商品
     */
    List<ActivityProductUploadError> uploadExcel(MultipartFile file, String headId, String uploadMethod,
                                                 String repeatProduct, String activityType) throws Exception;

    void validProductInfo(String headId);

    int getCountByHeadId(String headId);

    int validProduct(String headId);

    List<String> getGroupIds(Long headId);

    boolean checkProductId(String headId, String productId);

    boolean ifCalculate(String headId);

    List<String> getNotValidProductCount(Long headId);
}
