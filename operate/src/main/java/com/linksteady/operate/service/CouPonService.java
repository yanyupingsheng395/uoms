package com.linksteady.operate.service;

import com.linksteady.operate.domain.CouponInfo;
import com.linksteady.operate.domain.SmsTemplate;

import java.util.List;

/**
 * Created by hxcao on 2019-04-29
 */
public interface CouPonService {

    List<CouponInfo> getList(int startRow, int endRow);

    int getTotalCount();

}
