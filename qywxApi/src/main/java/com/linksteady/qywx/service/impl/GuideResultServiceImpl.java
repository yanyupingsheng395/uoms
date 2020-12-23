package com.linksteady.qywx.service.impl;

import com.linksteady.qywx.domain.GuideResult;
import com.linksteady.qywx.service.GuideResultService;
import org.springframework.stereotype.Service;

/**
 * @author hxcao
 * @date 2020/9/17
 */
@Service
public class GuideResultServiceImpl implements GuideResultService {

    @Override
    public GuideResult getResultData(String followUserId, int startDt, int endDt) {

        return null;
    }

    @Override
    public int getTotalCnt(String followUserId, int dayWid) {
        return 0;
    }

    @Override
    public double getTotalOrderAmount(int startDt, int endDt) {
        return 0;
    }
}
