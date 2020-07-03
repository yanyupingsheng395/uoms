package com.linksteady.qywx.service;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/7/2
 */
public interface UserTaskService {

    Map<String, Object> getUserData(String userId, String productId);

    List<Map<String, Object>> getProductData(String userId);

    List<Map<String, Object>> getUserBuyHistory(String userId);
}
