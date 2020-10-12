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
    List<Map<String, Object>> getUserTodayStatus(String userId, String productId);

    String getUserLastBuyDt(String productId, String userId);

    List<Map<String, String>> getUserTimes(String userId);

    List<Map<String, String>> getCouponListOfProduct(String userId, String productId);

    List<Map<String, Object>> getProductData(String userId);

    List<UserBuyHistory> getUserBuyHistory(String userId);

    List<SpuInfo> getSpuList(String userId);

    UserPurchStatsVO getPurchStats(String userId);

    UserPurchSpuStatsVO getPurchSpuStats(String userId, long spuId);

    String getSpuName(long spuId);

    String getFirstBuyDate(String userId);
}
