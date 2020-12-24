package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.GuideResult;
import com.linksteady.qywx.vo.GuideResultPurchInfoVO;

public interface GuideResultMapper {

    GuideResult getResultData(String followUserId, int startDt, int endDt);

    int getTotalCnt(String followUserId, int dayWid);

    int getAddCnt(String followUserId, int startDt, int endDt);

    GuideResultPurchInfoVO getPurchInfo(String followUserId, int startDt, int endDt);

    double getTotalOrderAmount(int startDt, int endDt);

    String getMinDayWid();
}
