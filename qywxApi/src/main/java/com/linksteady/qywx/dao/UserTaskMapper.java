package com.linksteady.qywx.dao;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/7/2
 */
public interface UserTaskMapper {

    /**
     * 获取用户今日所处的状态
     * @return
     */
    List<Map<String, Object>> getUserTodayStatus(String userId, String productId);

    String getUserLastBuyDt(String productId, String userId);

    List<Map<String, String>> getUserTimes(String userId);

    List<Map<String, String>> getCouponListOfProduct(String userId, String productId);

    List<Map<String, Object>> getProductData(String userId);

    List<Map<String, Object>> getUserBuyHistory(String userId);
}
