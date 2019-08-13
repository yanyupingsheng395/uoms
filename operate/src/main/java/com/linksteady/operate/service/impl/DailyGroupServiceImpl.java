package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.operate.dao.DailyEffectMapper;
import com.linksteady.operate.dao.DailyExecuteMapper;
import com.linksteady.operate.dao.DailyGroupMapper;
import com.linksteady.operate.dao.DailyMapper;
import com.linksteady.operate.domain.DailyExecute;
import com.linksteady.operate.domain.DailyGroup;
import com.linksteady.operate.service.DailyGroupService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 日运营群组
 * @author hxcao
 * @date 2019-07-31
 */
@Service
public class DailyGroupServiceImpl implements DailyGroupService {

    @Autowired
    private DailyGroupMapper dailyGroupMapper;

    @Autowired
    private DailyMapper dailyMapper;

    @Override
    public List<DailyGroup> getDataList(String headId, int start, int end) {
        return dailyGroupMapper.getDataList(headId, start, end);
    }

    @Override
    public void updateIsChecked(String headId, List<DailyGroup> groupList) {
        dailyGroupMapper.updateIsChecked(headId, groupList);
    }

    @Override
    public List<Map<String, Object>> getSelectedGroup(String headId, String activeIds, String growthIds) {
        List<String> activeIdList = Lists.newArrayList();
        List<String> growthIdList = Lists.newArrayList();
        boolean ifCheck = false;
        if(activeIds != null) {
            activeIdList = Arrays.asList(StringUtils.split(activeIds, ","));
        }
        if(growthIds != null) {
            growthIdList = Arrays.asList(StringUtils.split(growthIds, ","));
        }

        if(activeIds == null && growthIds == null) {
            return dailyGroupMapper.getSelectedGroupByIsCheck(headId);
        }

        return dailyGroupMapper.getSelectedGroup(headId, activeIdList, growthIdList);
    }

    @Override
    public List<String> getDefaultActive(String headId) {
        List<String> activeIds = dailyGroupMapper.getDefaultActive(headId);
        return activeIds;
    }

    @Override
    public List<String> getDefaultGrowth(String headId) {
        List<String> growthIds = dailyGroupMapper.getDefaultGrowth(headId);
        return growthIds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setGroupCheck(String headId, String groupIds) {
        if(groupIds != null) {
            List<String> groupIdList = Arrays.asList(groupIds.split(","));
            dailyGroupMapper.setIsCheckIsTrue(headId, groupIdList);
            dailyGroupMapper.setIsCheckIsFalse(headId, groupIdList);

            // 更改实际选择人数
            int num = dailyGroupMapper.sumCheckedNum(headId);
            dailyMapper.updateActualNum(headId, num);
        }
    }

    @Override
    public int getGroupDataCount(String headId) {
        return dailyGroupMapper.getGroupDataCount(headId);
    }

    @Override
    public void setSmsCode(String headId, String groupId, String smsCode) {
        dailyGroupMapper.setSmsCode(headId, groupId, smsCode);
    }
}
