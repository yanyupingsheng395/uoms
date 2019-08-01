package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.DailyEffectMapper;
import com.linksteady.operate.domain.DailyEffect;
import com.linksteady.operate.service.DailyEffectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-08-01
 */
@Service
public class DailyEffectServiceImpl implements DailyEffectService {

    @Autowired
    private DailyEffectMapper dailyEffectMapper;

    @Override
    public List<DailyEffect> getPageList(int start, int end, String touchDt) {
        return dailyEffectMapper.getPageList(start, end, touchDt);
    }

    @Override
    public int getDataCount(String touchDt) {
        return dailyEffectMapper.getDataCount(touchDt);
    }
}
