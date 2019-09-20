package com.linksteady.operate.service;

import com.linksteady.operate.domain.DailyDetail;
import com.linksteady.operate.sms.domain.TaskInfo;
import com.linksteady.operate.vo.Echart;

import java.util.List;

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
     * 效果评估-个体效果分页
     * @param headId
     * @param start
     * @param end
     * @param userValue
     * @param pathActive
     * @param status
     * @return
     */
    List<DailyDetail> getUserEffect(String headId, int start, int end, String userValue, String pathActive, String status);

    /**
     * 效果评估记录数
     * @param headId
     * @param userValue
     * @param pathActive
     * @param status
     * @return
     */
    int getDataListCount(String headId, String userValue, String pathActive, String status);

    /**
     *
     * @param headId
     * @return
     */
//    int findCountByPushStatus(String headId);
}
