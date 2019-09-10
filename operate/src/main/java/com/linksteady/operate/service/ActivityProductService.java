package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityProduct;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-09-07
 */
public interface ActivityProductService {

    int getCount(String headId);

    List<ActivityProduct> getActivityProductListPage(int start, int end, String headId);

    void saveActivityProduct(String startDate, String endDate, String headId);

    void saveActivityProductBySql(String startDate, String endDate, String headId);

    /**
     * 用户数在优惠券上的分布图
     * @param productId
     * @param startDate
     * @param endDate
     * @return
     */
    Map<String, Object> getCouponBoxData(String productId, String startDate, String endDate);

}
