package com.linksteady.operate.service;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.ActivityProduct;
import com.linksteady.operate.domain.ActivityProductUploadError;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-09-07
 */
public interface ActivityProductService {

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
     * @param headId
     * @param stage
     * @param productIds
     */
    void deleteProduct(Long headId, String stage, String productIds);

    int validProductNum(Long headId, String stage);

    String generateProductShortUrl(String productId,String sourceType);

    int getSameProductCount(List<String> productIdList, Long headId, String stage);

    /**
     * 删除重复的活动商品
     * @param productList
     * @param headId
     * @param stage
     */
    void deleteRepeatData(List<ActivityProduct> productList, Long headId, String stage);

    /**
     * 获取第一条商品的信息
     * @return
     */
    ActivityProduct geFirstProductInfo(Long headId, String stage);


    void deleteData(Long headId);

    /**
     * 上传商品
     */
    List<ActivityProductUploadError> uploadExcel(MultipartFile file, String headId, String uploadMethod, String repeatProduct) throws Exception;

    void validProductInfo(String headId);

    int getCountByHeadId(String headId);

    int validProduct(String headId);

    List<String> getGroupIds(Long headId);
}
