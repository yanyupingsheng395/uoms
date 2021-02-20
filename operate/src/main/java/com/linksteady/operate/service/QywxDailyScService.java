package com.linksteady.operate.service;

import com.linksteady.operate.exception.SendCouponException;

public interface QywxDailyScService {

    void sendCouponToDailyUser(Long headId) throws SendCouponException;
}
