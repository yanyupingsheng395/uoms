package com.linksteady.operate.service.impl;

import com.linksteady.operate.service.TargetSplitAsyncService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TargetSplitAsyncServiceImpl implements TargetSplitAsyncService {

    @Async
    @Override
    public void targetSplit(int targetId) {

        //获取目标的详细信息

    }
}
