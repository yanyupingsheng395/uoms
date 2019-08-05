package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.DailyGroupMapper;
import com.linksteady.operate.dao.DailyMapper;
import com.linksteady.operate.domain.DailyGroup;
import com.linksteady.operate.domain.DailyInfo;
import com.linksteady.operate.service.DailyService;
import com.linksteady.operate.vo.Echart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-07-31
 */
@Service
public class DailyServiceImpl implements DailyService {

    @Autowired
    private DailyMapper dailyMapper;

    @Autowired
    private DailyGroupMapper dailyGroupMapper;

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
    @Transactional(rollbackFor = Exception.class)
    public void updateCheckNum(String headId, List<String> groupIds) {
        int num = dailyGroupMapper.sumCheckedNum(headId);
        dailyMapper.updateActualNum(headId, num);
    }

    @Override
    public String getStatusById(String headId) {
        return dailyMapper.getStatusById(headId);
    }
}
