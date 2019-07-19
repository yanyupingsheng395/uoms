package com.linksteady.operate.dao;

/**
 * Created by hxcao on 2019-06-05
 */
import com.linksteady.operate.common.util.UserOperaterMapper;
import com.linksteady.operate.vo.KpiInfoVo;

import java.util.List;

/**
 * 连带率
 */
public interface UserJoinRateMapper extends UserOperaterMapper {

    @Override
    Double getKpiOfDifferPeriod(String joinInfo, String whereInfo);

    @Override
    List<KpiInfoVo> getDatePeriodData(String period_name, String joinInfo, String whereInfo);

    @Override
    List<KpiInfoVo> getSpAndFpKpi(String period_name, String joinInfo, String whereInfo);

    @Override
    KpiInfoVo getSpAndFpKpiTotal(String period_name, String joinInfo, String whereInfo);

}
