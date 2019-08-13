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
 * @author hxcao
 * @date 2019-07-31
 */
@Slf4j
@Service
public class DailyDetailServiceImpl implements DailyDetailService {

    private final String[] RETENTION_CODE = {"target01", "target02", "target03"};

    //未选择
    private static final String IS_CHECK_0 = "check:0";
    //未触达
    private static final String IS_PUSH_0 = "push:0";
    //未转化
    private static final String IS_CONVERT_0 = "convert:0";
    //已转化
    private static final String IS_CONVERT_1 = "convert:1";

    @Autowired
    private DailyDetailMapper dailyDetailMapper;

    @Override
    public List<DailyDetail> getPageList(int start, int end, String headId, String groupIds, String pathActive, String sortColumn, String sortOrder) {
        List<String> groupIdList = Lists.newArrayList();
        if(groupIds != null) {
            groupIdList = Arrays.asList(groupIds.split(","));
        }
        return dailyDetailMapper.getPageList(start, end, headId, groupIdList, pathActive, sortColumn, sortOrder);
    }

    @Override
    public int getDataCount(String headId, String groupIds, String pathActive) {
        List<String> groupIdList = Lists.newArrayList();
        if(groupIds != null) {
            groupIdList = Arrays.asList(groupIds.split(","));
        }
        return dailyDetailMapper.getDataCount(headId, groupIdList, pathActive);
    }

    @Override
    public List<DailyDetail> getUserEffect(String headId, int start, int end, String userValue, String pathActive, String status) {
        String whereInfo = getWhereInfo(userValue, pathActive, status);
        List<DailyDetail> dataList = dailyDetailMapper.getUserEffect(headId, start, end, whereInfo);
        return dataList;
    }

    private String getWhereInfo(String userValue, String pathActive, String status) {
        StringBuffer sb = new StringBuffer();
        if(StringUtils.isNotEmpty(status)) {
            // 未选择
            if(status.equals(IS_CHECK_0)) {
                sb.append(" and t1.is_check = '0'");
            }
            if(status.equals(IS_PUSH_0)) {
                sb.append(" and t1.is_push = '0' and t1.is_check = '1'");
            }

            if(status.equals(IS_CONVERT_0)) {
                sb.append(" and t1.is_push = '1' and t1.IS_CONVERSION = '0'");
            }

            if(status.equals(IS_CONVERT_1)) {
                sb.append(" and t1.IS_CONVERSION = '1'");
            }
        }

        if(StringUtils.isNotEmpty(userValue)) {
            sb.append(" and t1.user_value = '" + userValue + "'");
        }
        if(StringUtils.isNotEmpty(pathActive)) {
            sb.append(" and t1.PATH_ACTIV = '" + pathActive + "'");
        }
        return sb.toString();
    }

    @Override
    public int getDataListCount(String headId, String userValue, String pathActive, String status) {
        String whereInfo = getWhereInfo(userValue, pathActive, status);
        return dailyDetailMapper.getDataListCount(headId, whereInfo);
    }
}
