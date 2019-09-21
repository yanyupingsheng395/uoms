package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.DailyDetailMapper;
import com.linksteady.operate.domain.DailyDetail;
import com.linksteady.operate.service.DailyDetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 群组用户
 * @author hxcao
 * @date 2019-07-31
 */
@Slf4j
@Service
public class DailyDetailServiceImpl implements DailyDetailService {

    @Autowired
    private DailyDetailMapper dailyDetailMapper;

    /**
     * 每日运营用户列表分页
     * @param start
     * @param end
     * @param headId
     * @param userValue
     * @param pathActive
     * @return
     */
    @Override
    public List<DailyDetail> getPageList(int start, int end, String headId, String userValue, String pathActive) {
        return dailyDetailMapper.getPageList(start, end, headId, userValue, pathActive);
    }

    /**
     * 每日运营明细记录数
     * @param headId
     * @param userValue
     * @param pathActive
     * @return
     */
    @Override
    public int getDataCount(String headId, String userValue, String pathActive) {
        return dailyDetailMapper.getDataCount(headId, userValue, pathActive);
    }

    /**
     * 根据选择的状态拼接SQL where条件
     * @param userValue
     * @param pathActive
     * @return
     */
    private String getWhereInfo(String userValue, String pathActive, String isConvert) {
        StringBuffer sb = new StringBuffer();
        if(StringUtils.isNotEmpty(isConvert)) {
            sb.append(" and is_conversion = '" + isConvert + "'");
        }
        if(StringUtils.isNotEmpty(userValue)) {
            sb.append(" and user_value = '" + userValue + "'");
        }
        if(StringUtils.isNotEmpty(pathActive)) {
            sb.append(" and path_active = '" + pathActive + "'");
        }
        return sb.toString();
    }


    /**
     * 策略列表用户数
     * @param start
     * @param end
     * @param headId
     * @return
     */
    @Override
    public List<DailyDetail> getStrategyPageList(int start, int end, String headId) {
        return dailyDetailMapper.getStrategyPageList(start, end, headId);
    }

    /**
     * 策略列表记录数
     * @param headId
     * @return
     */
    @Override
    public int getStrategyCount(String headId) {
        return dailyDetailMapper.getStrategyCount(headId);
    }

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
    @Override
    public List<DailyDetail> getUserEffect(String headId, int start, int end, String userValue, String pathActive, String status) {
        String whereInfo = getWhereInfo(userValue, pathActive, status);
        List<DailyDetail> dataList = dailyDetailMapper.getUserEffect(headId, start, end, whereInfo);
        return dataList;
    }

    /**
     * 效果评估记录数
     * @param headId
     * @param userValue
     * @param pathActive
     * @param status
     * @return
     */
    @Override
    public int getDataListCount(String headId, String userValue, String pathActive, String status) {
        String whereInfo = getWhereInfo(userValue, pathActive, status);
        return dailyDetailMapper.getDataListCount(headId, whereInfo);
    }

    /**
     * 获取headId对应的短信内容列表
     * @param headId
     * @return
     */
    @Override
    public List<Map<String, Object>> getContentList(String headId) {
        return dailyDetailMapper.getContentList(headId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePushOrderPeriod(String headId, String pushOrderPeriod) {
        dailyDetailMapper.updatePushOrderPeriod(headId, pushOrderPeriod);
    }
}
