package com.linksteady.operate.dao;

/**
 * Created by hxcao on 2019-06-05
 *
 * @author cao
 */
import com.linksteady.operate.util.UserOperaterMapper;
import com.linksteady.operate.vo.KpiInfoVo;
import java.util.List;

/**
 * 用户数
 */
public interface UserCntMapper extends UserOperaterMapper {

    @Override
    Double getKpiOfDifferPeriod(String joinInfo, String whereInfo);

    @Override
    List<KpiInfoVo> getDatePeriodData(String period_name, String joinInfo, String whereInfo);

    @Override
    List<KpiInfoVo> getSpAndFpKpi(String period_name, String joinInfo, String whereInfo);

    @Override
    KpiInfoVo getSpAndFpKpiTotal(String period_name, String joinInfo, String whereInfo);
}
