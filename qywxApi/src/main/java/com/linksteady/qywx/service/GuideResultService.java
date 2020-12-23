package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.GuideResult;
/**
 * @author hxcao
 * @date 2020/9/17
 */
public interface GuideResultService {

    GuideResult getResultData(String followUserId, int startDt, int endDt);

    int getTotalCnt(String followUserId,int dayWid);

    double getTotalOrderAmount(int startDt,int endDt);
}
