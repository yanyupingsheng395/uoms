package com.linksteady.mdss.service;

import com.linksteady.mdss.domain.DiagHandleInfo;
import com.linksteady.mdss.domain.DiagResultInfo;

/**
 * 诊断操作统一接口
 * @author  huang
 */
public interface DiagOpService {

    /**
     * 统一的处理类
     * @param diagHandleInfo 封装操作信息的类
     * @return 返回诊断结果数据
     */
    DiagResultInfo process(DiagHandleInfo diagHandleInfo);
}
