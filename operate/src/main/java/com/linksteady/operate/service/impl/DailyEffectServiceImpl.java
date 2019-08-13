package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.DailyEffectMapper;
import com.linksteady.operate.dao.DailyMapper;
import com.linksteady.operate.domain.DailyEffect;
import com.linksteady.operate.service.DailyEffectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-08-01
 */
@Service
public class DailyEffectServiceImpl implements DailyEffectService {

    @Autowired
    private DailyEffectMapper dailyEffectMapper;

    @Autowired
    private DailyMapper dailyMapper;

    @Override
    public List<DailyEffect> getPageList(int start, int end, String touchDt) {
        return dailyEffectMapper.getPageList(start, end, touchDt);
    }

    @Override
    public DailyEffect getKpiStatis(String headId) {
        return dailyEffectMapper.getKpiStatis(headId);
    }

    /**
     * 更行执行率
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateExecuteRate(String headId) {
        DecimalFormat df = new DecimalFormat("#");
        Map<String, Object> totalAndOptMap = dailyMapper.getTotalNum(headId);
        Double executeRate = ((BigDecimal)totalAndOptMap.get("OPT")).doubleValue()/((BigDecimal)totalAndOptMap.get("TOTAL")).doubleValue() * 100D;
        dailyEffectMapper.updateExecuteAndLossRate(df.format(executeRate), headId);
    }
}
