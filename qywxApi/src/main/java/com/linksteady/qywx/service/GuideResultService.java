package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.GuideResult;
import com.linksteady.qywx.vo.GuideResultPurchInfoVO;

/**
 * @author hxcao
 * @date 2020/9/17
 */
public interface GuideResultService {

    GuideResult getResultData(String followUserId, int startDt, int endDt);

    int getTotalCnt(String followUserId,int dayWid);

    int getAddCnt(String followUserId,int startDt,int endDt);

    GuideResultPurchInfoVO getPurchInfo(String followUserId, int startDt, int endDt);

    double getTotalOrderAmount(int startDt,int endDt);

    String getMinDayWid();
}
