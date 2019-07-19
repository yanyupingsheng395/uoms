package com.linksteady.mdss.service;

import com.linksteady.mdss.domain.TargetInfo;
import com.linksteady.mdss.vo.TgtReferenceVO;

import java.util.List;
import java.util.Map;

public interface TgtCalculateService {

     String TARGET_PERIOD_YEAR="year";
     String  TARGET_PERIOD_MONTH="month";
     String TARGET_PERIOD_DAY="day";

    /**
     * 获取参照信息
     * @param period
     * @param startDt
     * @param endDt
     * @param dimInfo
     * @return
     */
    List<TgtReferenceVO> getReferenceData(String period, String startDt, String endDt, Map<String,String> dimInfo);

    /**
     * 对某个目标的完成情况进行计算
     * @param targetInfo
     */
    void calculateTarget(TargetInfo targetInfo);


    /**
     * 对目标进行拆解
     * @param targetId
     */
    void targetSplit(Long targetId) throws Exception;

}
