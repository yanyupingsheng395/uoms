package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.SpuInfo;
import com.linksteady.qywx.domain.UserBuyHistory;
import com.linksteady.qywx.vo.UserPurchStatsVO;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/7/2
 */
public interface UserTaskService {

    Map<String, Object> getUserGuideInfo(String operateUserId, String productId);

    List<Map<String, Object>> getRecProductList(String operateUserId);

    List<UserBuyHistory> getUserBuyHistory(Long userId,long spuId);

    /**
     * 获取当前用户涉及的spu列表
     */
    List<SpuInfo> getSpuList(Long userId);

    /**
     * 获取用户在类目上的购买统计数据
     */
    Map<String, String> getUserStatis(Long userId);

    /**
     * 获取用户在类目上的购买统计数据
     */
    Map<String, String> getUserStatis(Long userId,long spuId,String spuName);

    /**
     * 获取类目的名称
     */
    String getSpuName(long spuId);

    /**
     * 获取用户的首购日期
     */
    String getFirstBuyDate(Long userId);

    /**
     * 获取用户在类目上的价值
     */
    String getUserValue(Long userId,long spuId);

    /**
     * 获取用户在类目上的生命周期阶段
     */
    String getLifeCycle(Long userId,long spuId);
}
