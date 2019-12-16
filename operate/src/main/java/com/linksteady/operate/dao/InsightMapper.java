package com.linksteady.operate.dao;

import com.linksteady.common.domain.Ztree;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-12-07
 */
public interface InsightMapper {

    List<Ztree> getSpuTree();

    /**
     * 获取product树
     * @param spuWid
     * @return
     */
    List<Ztree> getProductTree(@Param("spuWid") String spuWid);

    List<Map<String, Object>> retentionInPurchaseTimes(String type, String id, int period);

    List<Map<String, Object>> unitPriceInPurchaseTimes(String type, String id, int period);

    List<Map<String, Object>> joinRateInPurchaseTimes(String type, String id, int period);

    List<Map<String, Object>> categoryInPurchaseTimes(String type, String id, int period);

    List<Map<String, Object>> periodInPurchaseTimes(String type, String id, int period);

    List<Map<String, Object>> getSpuConvertRateProducts(String id, String type, String purchOrder);
}
