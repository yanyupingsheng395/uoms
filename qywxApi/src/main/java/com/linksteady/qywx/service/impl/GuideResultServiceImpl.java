package com.linksteady.qywx.service.impl;

import com.linksteady.qywx.dao.GuideResultMapper;
import com.linksteady.qywx.domain.GuideResult;
import com.linksteady.qywx.service.GuideResultService;
import com.linksteady.qywx.vo.GuideResultPurchInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hxcao
 * @date 2020/9/17
 */
@Service
public class GuideResultServiceImpl implements GuideResultService {

    @Autowired
    GuideResultMapper guideResultMapper;

    @Override
    public GuideResult getResultData(String followUserId, int startDt, int endDt) {
        return guideResultMapper.getResultData(followUserId,startDt,endDt);
    }

    /**
     * 获取添加用户数
     * @param followUserId
     * @param dayWid
     * @return
     */
    @Override
    public int getTotalCnt(String followUserId, int dayWid) {
        return guideResultMapper.getTotalCnt(followUserId,dayWid);
    }

    @Override
    public int getAddCnt(String followUserId, int startDt, int endDt) {
        return guideResultMapper.getAddCnt(followUserId,startDt,endDt);
    }

    @Override
    public GuideResultPurchInfoVO getPurchInfo(String followUserId, int startDt, int endDt) {
        return guideResultMapper.getPurchInfo(followUserId,startDt,endDt);
    }

    @Override
    public double getTotalOrderAmount(int startDt, int endDt) {
        return guideResultMapper.getTotalOrderAmount(startDt,endDt);
    }

    @Override
    public String getMinDayWid() {
        return guideResultMapper.getMinDayWid();
    }
}
