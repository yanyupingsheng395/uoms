package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.SpuInfo;
import com.linksteady.qywx.domain.UserBuyHistory;
import com.linksteady.qywx.vo.UserPurchSpuStatsVO;
import com.linksteady.qywx.vo.UserPurchStatsVO;

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
    List<Map<String, Object>> getUserTodayStatus(String operateUserId, String productId);

    String getUserLastBuyDt(String productId, String operateUserId);

    List<Map<String, String>> getUserTimes(String operateUserId);

    List<Map<String, String>> getCouponListOfProduct(String operateUserId, String productId);

    List<Map<String, Object>> getRecProductList(String operateUserId);

    List<UserBuyHistory> getUserBuyHistory(Long userId,long spuId);

    List<SpuInfo> getSpuList(Long userId);

    UserPurchStatsVO getPurchStats(Long userId);

    UserPurchSpuStatsVO getPurchSpuStats(Long userId, long spuId);

    String getSpuName(long spuId);

    String getFirstBuyDate(Long userId);

    String getUserValue(Long userId,long spuId);

    String getLifeCycle(Long userId,long spuId);
}
