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
     * 每日运营用户列表分页
     * @param start
     * @param end
     * @param headId
     * @param userValue
     * @param pathActive
     * @return
     */
    List<DailyDetail> getPageList(int start, int end, String headId, String userValue, String pathActive);

    /**
     * 每日运营明细记录数
     * @param headId
     * @param userValue
     * @param pathActive
     * @return
     */
    int getDataCount(String headId, String userValue, String pathActive);

    /**
     * 策略列表用户数
     * @param start
     * @param end
     * @param headId
     * @return
     */
    List<DailyDetail> getStrategyPageList(int start, int end, String headId);

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
     * 更新短信推送时段
     * @param headId
     * @param pushOrderPeriod
     */
    void updatePushOrderPeriod(String headId, String pushOrderPeriod);

    /**
     * copy到推送列表中
     * @param headId
     * @return
     */
    void copyToPushList(String headId);

    /**
     * 生成短信文案
     * @param headerId
     */
    String generatePushList(String headerId);

    /**
     * 保存文案信息到临时表
     * @param targetList
     */
    void insertPushContentTemp( List<DailyDetail> targetList);

    /**
     * 清空保存文案的临时表
     */
    void deletePushContentTemp(String headerId);

}
