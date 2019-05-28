package com.linksteady.operate.service;

import com.linksteady.operate.domain.TargetInfo;
import com.linksteady.operate.vo.TgtReferenceVO;

import java.util.List;
import java.util.Map;

public interface TgtCalculateService {

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
}
