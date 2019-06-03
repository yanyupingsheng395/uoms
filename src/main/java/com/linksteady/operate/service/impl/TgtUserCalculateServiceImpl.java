package com.linksteady.operate.service.impl;

import com.linksteady.operate.domain.TargetInfo;
import com.linksteady.operate.service.TgtCalculateService;
import com.linksteady.operate.vo.TgtReferenceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 用户数目标计算的服务类
 * @author huang
 */
@Slf4j
@Service
public class TgtUserCalculateServiceImpl implements TgtCalculateService {
    @Override
    public List<TgtReferenceVO> getReferenceData(String period, String startDt, String endDt, Map<String, String> dimInfo) {
        return null;
    }

    @Override
    public void calculateTarget(TargetInfo targetInfo) {

    }

    @Override
    public void targetSplit(Long targetId) {

    }
}
