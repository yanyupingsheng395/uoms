package com.linksteady.operate.service.impl;

import com.linksteady.common.service.OpenApiService;
import com.linksteady.common.service.TaskApiService;
import com.linksteady.operate.task.CalculateAllTargetTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author hxcao
 * @date 2019-07-08
 */
@Service
public class TaskApiServiceImpl implements TaskApiService {

    @Autowired
    private CalculateAllTargetTask calculateAllTargetTask;

    @Override
    public void calculateGmv() {
        calculateAllTargetTask.calculate();
    }
}
