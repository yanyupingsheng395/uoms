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
    public OperateResult getResultData(String startDt, String endDt) {
        OperateResult operateResult = new OperateResult();
        Field[] declaredFields = operateResult.getClass().getDeclaredFields();
        for (int i = 0; i < declaredFields.length; i++) {
            Field field = declaredFields[i];
            try {
                Method method = operateResult.getClass().getDeclaredMethod("set" + StringUtils.capitalize(field.getName()), field.getType());
                method.invoke(operateResult, new Random().nextInt(100));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return operateResult;
    }
}
