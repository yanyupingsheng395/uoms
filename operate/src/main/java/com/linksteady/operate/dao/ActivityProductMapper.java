package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivityProduct;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-09-07
 */
public interface ActivityProductMapper {

    int getCount(String headId);

    List<ActivityProduct> getActivityProductListPage(int start, int end, String headId);

    void saveDataList(List<ActivityProduct> productList);

    Map<String, Object> getLowestProductPrice(String productId);
}
