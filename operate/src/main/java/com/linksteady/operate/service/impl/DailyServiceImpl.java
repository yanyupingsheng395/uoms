package com.linksteady.operate.service.impl;

import com.google.common.collect.Maps;
import com.linksteady.operate.dao.DailyMapper;
import com.linksteady.operate.domain.DailyGroupTemplate;
import com.linksteady.operate.domain.DailyInfo;
import com.linksteady.operate.service.DailyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 日运营头表
 *
 * @author hxcao
 * @date 2019-07-31
 */
@Service
public class DailyServiceImpl implements DailyService {

    @Autowired
    private DailyMapper dailyMapper;

    @Override
    public List<DailyInfo> getPageList(int start, int end, String touchDt) {
        return dailyMapper.getPageList(start, end, touchDt);
    }

    @Override
    public int getTotalCount(String touchDt) {
        return dailyMapper.getTotalCount(touchDt);
    }

    @Override
    public List<DailyInfo> getTouchPageList(int start, int end, String touchDt) {
        return dailyMapper.getTouchPageList(start, end, touchDt);
    }

    @Override
    public int getTouchTotalCount(String touchDt) {
        return dailyMapper.getTouchTotalCount(touchDt);
    }


    @Override
    public Map<String, Object> getTipInfo(String headId) {
        return dailyMapper.getTipInfo(headId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(String headId, String status) {
        dailyMapper.updateStatus(headId, status);
    }


    @Override
    public String getStatusById(String headId) {
        return dailyMapper.getStatusById(headId);
    }

    @Override
    public DailyInfo getKpiVal(String headId) {
        DailyInfo dailyInfo = dailyMapper.getKpiVal(headId);
        return dailyInfo;
    }

    @Override
    public Map<String, Object> getCurrentAndTaskDate(String headId) {
        Map<String, Object> result = Maps.newHashMap();
        String touchDt = dailyMapper.getTouchDt(headId);
        result.put("taskDt", touchDt);
        result.put("currentDt", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        return result;
    }

    /**
     * 获取用户组配置表的分页数据
     * @param start
     * @param end
     * @return
     */
    @Override
    public List<DailyGroupTemplate> getUserGroupListPage(int start, int end) {
        return dailyMapper.getUserGroupListPage(start, end);
    }

    /**
     * 获取用户组配置表的数据
     * @return
     */
    @Override
    public int getUserGroupCount() {
        return dailyMapper.getUserGroupCount();
    }

    /**
     * 设置短信模板code
     * @param groupId
     * @param smsCode
     */
    @Override
    public void setSmsCode(String groupId, String smsCode) {
        if(StringUtils.isNotEmpty(groupId)) {
            List<String> groupIds = Arrays.asList(groupId.split(","));
            dailyMapper.setSmsCode(groupIds, smsCode);
        }
    }

    @Override
    public boolean validUserGroup() {
        // 获取短信内容为空的情况
        int notValidCount = dailyMapper.validUserGroup();
        if(notValidCount > 0) {
            return false;
        }
        return true;
    }
}
