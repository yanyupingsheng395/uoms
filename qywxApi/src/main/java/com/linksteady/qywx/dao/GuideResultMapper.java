package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.GuideResult;

public interface GuideResultMapper {

    GuideResult getResultData(String followUserId, int startDt, int endDt);

    int getTotalCnt(String followUserId, int dayWid);

    double getTotalOrderAmount(int startDt, int endDt);
}
