package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.GuideResult;
/**
 * @author hxcao
 * @date 2020/9/17
 */
public interface GuideResultService {

    GuideResult getResultData(String userId, String startDt, String endDt);
}
