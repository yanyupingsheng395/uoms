package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.operate.dao.DailyDetailMapper;
import com.linksteady.operate.domain.DailyDetail;
import com.linksteady.operate.service.DailyDetailService;
import com.linksteady.operate.vo.Echart;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 群组用户
 * @author hxcao
 * @date 2019-07-31
 */
@Slf4j
@Service
public class DailyDetailServiceImpl implements DailyDetailService {

    private final String[] RETENTION_CODE = {"target01", "target02", "target03"};

    /**
     * 未选择
     */
    private static final String IS_CHECK_0 = "check:0";
    /**
     * 未触达
     */
    private static final String IS_PUSH_0 = "push:0";
    /**
     * 未转化
     */
    private static final String IS_CONVERT_0 = "convert:0";
    /**
     * 已转化
     */
    private static final String IS_CONVERT_1 = "convert:1";

    @Autowired
    private DailyDetailMapper dailyDetailMapper;

    @Override
    public List<DailyDetail> getPageList(int start, int end, String headId, String userValue, String pathActive) {
        return dailyDetailMapper.getPageList(start, end, headId, userValue, pathActive);
    }

    @Override
    public int getDataCount(String headId, String userValue, String pathActive) {
        return dailyDetailMapper.getDataCount(headId, userValue, pathActive);
    }



    /**
     * 根据选择的状态拼接SQL where条件
     * @param userValue
     * @param pathActive
     * @param status
     * @return
     */
    private String getWhereInfo(String userValue, String pathActive, String isConvert) {
        StringBuffer sb = new StringBuffer();
        if(StringUtils.isNotEmpty(isConvert)) {
            sb.append(" and t1.is_conversion = '" + isConvert + "'");
        }
        if(StringUtils.isNotEmpty(userValue)) {
            sb.append(" and t1.user_value = '" + userValue + "'");
        }
        if(StringUtils.isNotEmpty(pathActive)) {
            sb.append(" and t1.path_activ = '" + pathActive + "'");
        }
        return sb.toString();
    }



    @Override
    public List<DailyDetail> getStrategyPageList(int start, int end, String headId) {
        return dailyDetailMapper.getStrategyPageList(start, end, headId);

    }

    @Override
    public int getStrategyCount(String headId) {
        return dailyDetailMapper.getStrategyCount(headId);
    }

    @Override
    public List<DailyDetail> getUserEffect(String headId, int start, int end, String userValue, String pathActive, String status) {
        String whereInfo = getWhereInfo(userValue, pathActive, status);
        List<DailyDetail> dataList = dailyDetailMapper.getUserEffect(headId, start, end, whereInfo);
        return dataList;
    }

    @Override
    public int getDataListCount(String headId, String userValue, String pathActive, String status) {
        String whereInfo = getWhereInfo(userValue, pathActive, status);
        return dailyDetailMapper.getDataListCount(headId, whereInfo);
    }
}
