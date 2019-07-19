package com.linksteady.operate.dao;

import com.linksteady.operate.domain.DatePeriodKpi;
import com.linksteady.operate.vo.ParamVO;
import com.linksteady.operate.vo.TemplateResult;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-06-15
 */
public interface SynGroupMapper {

    /**
     * 留存率 - 间隔月
     * @param paramVO
     * @param templateResult
     * @return
     */
    List<Map<String, Object>> getRetentionDMonth(ParamVO paramVO, TemplateResult templateResult);

    /**
     * 留存率 - 自然月
     * @param paramVO
     * @param templateResult
     * @return
     */
    List<DatePeriodKpi> getRetentionNMonth(ParamVO paramVO, TemplateResult templateResult);


    /**
     * 流失率 - 间隔月
     * @param paramVO
     * @param templateResult
     * @return
     */
    List<Map<String, Object>> getLossUserRateDMonth(ParamVO paramVO, TemplateResult templateResult);

    /**
     * 流失率 - 自然月
     * @param paramVO
     * @param templateResult
     * @return
     */
    List<DatePeriodKpi> getLossUserMonth(ParamVO paramVO, TemplateResult templateResult);

    /**
     * 留存用户数 - 间隔月
     * @param paramVO
     * @param templateResult
     * @return
     */
    List<Map<String, Object>> getRetainUserCountDMonth(ParamVO paramVO, TemplateResult templateResult);

    /**
     * 留存用户数 - 自然月
     * @param paramVO
     * @param templateResult
     * @return
     */
    List<DatePeriodKpi> getRetainUserCountNMonth(ParamVO paramVO, TemplateResult templateResult);

    /**
     * 客单价 - 间隔月
     * @param paramVO
     * @param templateResult
     * @return
     */
    List<Map<String, Object>> getUpriceDMonth(ParamVO paramVO, TemplateResult templateResult);

    /**
     * 件单价 - 自然月
     * @param paramVO
     * @param templateResult
     * @return
     */
    List<DatePeriodKpi> getUpriceNMonth(ParamVO paramVO, TemplateResult templateResult);

    /**
     * 客单价 - 间隔月
     * @param paramVO
     * @param templateResult
     * @return
     */
    List<Map<String, Object>> getPriceDMonth(ParamVO paramVO, TemplateResult templateResult);

    /**
     * 件单价 - 间隔月
     * @param paramVO
     * @param templateResult
     * @return
     */
    List<Map<String, Object>> getUnitPriceDMonth(ParamVO paramVO, TemplateResult templateResult);

    /**
     * 连带率 - 间隔月
     * @param paramVO
     * @param templateResult
     * @return
     */
    List<Map<String, Object>> getJoinRateDMonth(ParamVO paramVO, TemplateResult templateResult);

    /**
     * 购买频率 - 间隔月
     * @param paramVO
     * @param templateResult
     * @return
     */
    List<Map<String, Object>> getPurchFreqDMonth(ParamVO paramVO, TemplateResult templateResult);

    /**
     * 购买频率 - 自然月
     * @param paramVO
     * @param templateResult
     * @return
     */
    List<DatePeriodKpi> getPurchFreqNMonth(ParamVO paramVO, TemplateResult templateResult);

    /**
     * 连带率 - 自然月
     * @param paramVO
     * @param templateResult
     * @return
     */
    List<DatePeriodKpi> getJoinRateNMonth(ParamVO paramVO, TemplateResult templateResult);

    /**
     * 件单价 - 自然月
     * @param paramVO
     * @param templateResult
     * @return
     */
    List<DatePeriodKpi> getUnitPriceNMonth(ParamVO paramVO, TemplateResult templateResult);
    /**
     * 客单价 - 自然月
     * @param paramVO
     * @param templateResult
     * @return
     */
    List<DatePeriodKpi> getPriceNMonth(ParamVO paramVO, TemplateResult templateResult);

    /**
     * 流失用户数 - 间隔月
     * @param paramVO
     * @param templateResult
     * @return
     */
    List<Map<String, Object>> getLossUserDMonth(ParamVO paramVO, TemplateResult templateResult);

    /**
     * 流失用户数 - 自然月
     * @param paramVO
     * @param templateResult
     * @return
     */
    List<DatePeriodKpi> getLossUserNMonth(ParamVO paramVO, TemplateResult templateResult);
}
