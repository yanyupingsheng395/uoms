package com.linksteady.operate.service;

import com.linksteady.operate.domain.DailyPushInfo;

import java.util.List;

public interface PushMessageService {

    void push(List<DailyPushInfo> list);
}
