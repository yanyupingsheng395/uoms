package com.linksteady.qywx.service.impl;

import com.linksteady.qywx.domain.OperateResult;
import com.linksteady.qywx.service.OperateResultService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;

/**
 * @author hxcao
 * @date 2020/9/17
 */
@Service
public class OperateResultServiceImpl implements OperateResultService {

    @Override
    public OperateResult getResultData(String userId, String startDt, String endDt) {
        OperateResult operateResult = new OperateResult();
        return operateResult;
    }
}
