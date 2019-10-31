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

}
