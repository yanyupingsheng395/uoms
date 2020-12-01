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
    public GuideResult getResultData(String followUserId, String startDt, String endDt) {
        GuideResult guideResult = new GuideResult();
        return guideResult;
    }
}
