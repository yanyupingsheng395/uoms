package com.linksteady.operate.service;

import com.linksteady.operate.vo.ParamVO;

import java.util.Map;

/**
 * @author hxcao
 * @date 2019-06-15
 * 同期群数据
 */
public interface SynGroupService {
    /**
     * 留存率
     * @param spuId 品类ID
     * @return
     */
    Map<String, Object> getRetentionData(ParamVO paramVO, String spuId);

    /**
     * 流失用户数
     * @param paramVO
     * @param spuId
     * @return
     */
    Map<String, Object> getLossUser(ParamVO paramVO, String spuId);
    /**
     * 流失率
     * @param paramVO
     * @param spuId
     * @return
     */
    Map<String, Object> getLossUserRate(ParamVO paramVO, String spuId);

    /**
     * 留存用户数
     * @param paramVO
     * @param spuId
     * @return
     */
    Map<String, Object> getRetainUserCount(ParamVO paramVO, String spuId);

    /**
     *  客单价
     * @param paramVO
     * @param spuId
     * @return
     */
    Map<String, Object> getUpriceData(ParamVO paramVO, String spuId);

    /**
     * 订单价
     * @param paramVO
     * @param spuId
     * @return
     */
    Map<String, Object> getPriceData(ParamVO paramVO, String spuId);

    /**
     * 件单价
     * @param paramVO
     * @param spuId
     * @return
     */
    Map<String, Object> getUnitPriceData(ParamVO paramVO, String spuId);

    /**
     * 连带率
     * @param paramVO
     * @param spuId
     * @return
     */
    Map<String, Object> getJoinRateData(ParamVO paramVO, String spuId);

    /**
     * 购买频率
     * @param paramVO
     * @param spuId
     * @return
     */
    Map<String, Object> getPurchFreq(ParamVO paramVO, String spuId);
}
