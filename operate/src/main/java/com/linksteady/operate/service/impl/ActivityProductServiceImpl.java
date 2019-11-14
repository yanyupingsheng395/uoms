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

        generateProdMapping(String.valueOf(activityProduct.getHeadId()),activityProduct.getActivityStage());
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

        generateProdMapping(String.valueOf(activityProduct.getHeadId()),activityProduct.getActivityStage());
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

        generateProdMapping(headId,stage);
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
        return shortUrlService.genProdShortUrlByProdId(productId);
    }

    @Override
    public int getSameProductCount(List<String> productIdList, String headId, String stage) {
        return activityProductMapper.getSameProductCount(productIdList, headId, stage);
    }

    @Override
    public void deleteRepeatData(List<ActivityProduct> productList, String headId, String stage) {
        activityProductMapper.deleteRepeatData(productList, headId, stage);
        generateProdMapping(headId,stage);
    }

    @Override
    public ActivityProduct geFirstProductInfo(String headId, String stage) {
        return activityProductMapper.geFirstProductInfo(headId,stage);
    }

    @Override
    public void insertActivityProdMapping(String headId, String stage) {
         activityProductMapper.insertActivityProdMapping(headId,stage);
    }

    @Override
    public void deleteActivityProdMapping(String headId, String stage) {
         activityProductMapper.deleteActivityProdMapping(headId,stage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteData(String headId) {
        activityProductMapper.deleteData(headId);
        //删除映射数据
        activityProductMapper.deleteActivityProdMapping(headId,"");
    }

    /**
     * 生成映射数据
     * @param headId
     * @param stage
     */
    private void generateProdMapping(String headId, String stage)
    {
        deleteActivityProdMapping(headId,stage);
        insertActivityProdMapping(headId,stage);
    }
}
