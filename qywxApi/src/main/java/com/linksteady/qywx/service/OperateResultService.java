package com.linksteady.qywx.service;
import com.linksteady.qywx.domain.OperateResult;
/**
 * @author hxcao
 * @date 2020/9/17
 */
public interface OperateResultService {

    OperateResult getResultData(String userId, String startDt, String endDt);
}
