package com.linksteady.operate.dao;

import com.linksteady.operate.domain.DailyDetail;
import com.linksteady.operate.sms.domain.SmsInfo;
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
     * @param headId
     * @param userValue
     * @param pathActive
     * @return
     */
    int getDataCount(String headId, String userValue, String pathActive);

    /**
     * 每日运营策略分页
     * @param start
     * @param end
     * @param headId
     * @return
     */
    List<DailyDetail> getStrategyPageList(int start, int end, String headId);

    /**
     * 每日运营策略记录数
     * @param headId
     * @return
     */
    int getStrategyCount(String headId);

    List<DailyDetail> getUserEffect(String headId, int start, int end, String whereInfo);

    /**
     * 根据条件获取每日运营明细的记录数
     * @param headId
     * @param whereInfo
     * @return
     */
    int getDataListCount(String headId, String whereInfo);


    /**
     * 获取短信列表信息
     * @param headId
     * @return
     */
    List<SmsInfo> findSmsInfoByHeadId(String headId);

//    int findCountByPushStatus(String headId);
}
