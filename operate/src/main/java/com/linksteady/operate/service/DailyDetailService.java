package com.linksteady.operate.service;

import com.linksteady.operate.domain.DailyDetail;
import com.linksteady.operate.domain.DailyUserStats;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyDetailService {

    /**
     * 策略列表用户数
     * @param limit
     * @param offset
     * @param headId
     * @return
     */
    List<DailyDetail> getStrategyPageList(int limit, int offset, String headId);

    /**
     * 策略列表记录数
     * @param headId
     * @return
     */
    int getStrategyCount(String headId);


    /**
     * 获取短信内容列表
     * @param headId
     * @return
     */
    List<Map<String, Object>> getContentList(String headId);

    /**
     * 生成短信文案
     * @param headerId
     */
    void generatePushList(String headerId) throws Exception;

    /**
     * 保存文案信息到临时表
     * @param targetList
     */
    void insertPushContentTemp( List<DailyDetail> targetList);

}
