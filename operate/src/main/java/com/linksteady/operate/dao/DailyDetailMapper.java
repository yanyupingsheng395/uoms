package com.linksteady.operate.dao;

import com.linksteady.operate.domain.DailyDetail;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyDetailMapper {

    /**
     * 每日运营明细分页查询
     *
     * @param start
     * @param end
     * @param headId
     * @param userValue
     * @param pathActive
     * @return
     */
    List<DailyDetail> getPageList(int start, int end, String headId, String userValue, String pathActive);

    /**
     * 根据活跃度和价值获取当前head_ID下符合条件的记录数
     *
     * @param headId
     * @param userValue
     * @param pathActive
     * @return
     */
    int getDataCount(String headId, String userValue, String pathActive);

    /**
     * 每日运营策略分页
     *
     * @param start
     * @param end
     * @param headId
     * @return
     */
    List<DailyDetail> getStrategyPageList(int start, int end, String headId);

    /**
     * 每日运营策略记录数
     *
     * @param headId
     * @return
     */
    int getStrategyCount(String headId);

    /**
     * 获取短信列表
     *
     * @param headId
     * @return
     */
    List<Map<String, Object>> getContentList(String headId);

    /**
     * 将当前每日运营的推送数据复制到推送列表中
     */
    void copyToPushList(String headId);

    void updatePushOrderPeriod(String headId);

    /**
     * 获取当前header_id下的用户列表
     * @param headId
     * @return
     */
    int getUserCount(@Param("headId") String headId);

    /**
     * 分页获取当header_id下选中的用户名单
     * @param headId
     * @param start
     * @param end
     * @return
     */
    List<DailyDetail> getUserList(@Param("headId") String headId, @Param("start") int start, @Param("end") int end);

    /**
     * 保存推送的文案信息到临时表
     */
    void insertPushContentTemp(@Param("list") List<DailyDetail> list);

    /**
     * 删除文案临时表中的数据
     */
    void deletePushContentTemp(@Param("headId") String headId);

    /**
     * 将文案内容从临时表更新到每日运营明细表
     */
    void updatePushContentFromTemp(@Param("headId") String headId);

}
