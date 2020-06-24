package com.linksteady.operate.dao;

/**
 * Created by hxcao on 2019-06-05
 */


import com.linksteady.operate.util.UserOperaterMapper;
import com.linksteady.operate.vo.KpiInfoVO;

import java.util.List;

/**
 * 件单价
 */
public interface SpriceMapper extends UserOperaterMapper {
    @Override
    Double getKpiOfDifferPeriod(String joinInfo, String whereInfo);

    @Override
    List<KpiInfoVO> getDatePeriodData(String period_name, String joinInfo, String whereInfo);

    @Override
    List<KpiInfoVO> getSpAndFpKpi(String period_name, String joinInfo, String whereInfo);

    @Override
    KpiInfoVO getSpAndFpKpiTotal(String period_name, String joinInfo, String whereInfo);
}
