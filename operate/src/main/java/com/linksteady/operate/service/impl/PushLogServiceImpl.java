package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.PushLogMapper;
import com.linksteady.operate.domain.PushLog;
import com.linksteady.operate.service.PushLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Service
@Slf4j
public class PushLogServiceImpl implements PushLogService {

    @Autowired
    PushLogMapper pushLogMapper;

    @Override
    public List<PushLog> getPushLogList(int day) {
        return pushLogMapper.getList(day);
    }
}
