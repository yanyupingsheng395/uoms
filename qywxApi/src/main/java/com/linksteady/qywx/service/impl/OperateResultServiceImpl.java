package com.linksteady.qywx.service.impl;

import com.linksteady.qywx.domain.OperateResult;
import com.linksteady.qywx.service.OperateResultService;
import org.springframework.stereotype.Service;

/**
 * @author hxcao
 * @date 2020/9/17
 */
@Service
public class OperateResultServiceImpl implements OperateResultService {

    @Override
    public OperateResult getResultData(String startDt, String endDt) {
        OperateResult operateResult = new OperateResult();
        operateResult.setAddUserCnt(1);
        operateResult.setAllPushAmount(1);
        operateResult.setAllPushAmountScale(1);
        operateResult.setPurchasedRate(1);
        operateResult.setPurchasedUserCnt(1);
        operateResult.setPushAndPurchaseUserCnt(1);
        operateResult.setPushConvertRate(1);
        operateResult.setUserTotalCnt(1);
        operateResult.setReceiveMsgCnt(1);
        return operateResult;
    }
}
