package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.SpuInfo;
import com.linksteady.qywx.domain.UserBuyHistory;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/7/2
 */
public interface UserTaskService {

    Map<String, Object> getUserData(String userId, String productId);

    List<Map<String, Object>> getProductData(String userId);

    List<UserBuyHistory> getUserBuyHistory(String userId);

    /**
     * 获取当前用户涉及的spu列表
     */
    List<SpuInfo> getSpuList(String userId);

    Map<String,String> getUserStatis(String userId);
}
