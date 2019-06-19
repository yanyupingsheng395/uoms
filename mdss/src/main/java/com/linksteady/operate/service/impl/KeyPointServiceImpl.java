package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.KeyPointMapper;
import com.linksteady.operate.domain.KeyPointMonth;
import com.linksteady.operate.domain.KeyPointYear;
import com.linksteady.operate.service.KeyPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
public class KeyPointServiceImpl  implements KeyPointService {

    @Autowired
    private KeyPointMapper keyPointMapper;


    @Override
    public KeyPointMonth getKeyPointMonthData(String month)
    {
       return  keyPointMapper.getKeyPointMonthData(month);

    }

    @Override
    public List<Map<String, Object>> getGMVByDay(String month) {
        return keyPointMapper.getGMVByDay(month);
    }

    @Override
    public KeyPointYear getKeyPointYearData(String year) {
        return keyPointMapper.getKeyPointYearData(year);
    }

    @Override
    public List<Map<String, Object>> getGMVTrendByMonth(String year) {
        return keyPointMapper.getGMVTrendByMonth(year);
    }

    @Override
    public List<Map<String, Object>> getGMVCompareByMonth(String year) {
        return keyPointMapper.getGMVCompareByMonth(year);
    }

    @Override
    public List<Map<String, Object>> getProfitRateByMonth(String year) {
        return keyPointMapper.getProfitRateByMonth(year);
    }

    @Override
    public String isFixProfitByMonth(String month) {
        return "Y";
    }

    @Override
    public String isFixProfitByYear(String year) {
        return "Y";
    }

    @Override
    public List<Map<String, Object>> getKeypointHint(String periodtype, String periodvalue) {
        if(!StringUtils.isEmpty(periodtype)&&"year".equals(periodtype))
        {
            return keyPointMapper.getKeypointHint(periodvalue);
        }else
        {
            return keyPointMapper.getKeypointHintByMonth(periodvalue);
        }
    }


}
