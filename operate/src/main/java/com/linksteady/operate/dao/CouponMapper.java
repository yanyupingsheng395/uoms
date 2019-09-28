package com.linksteady.operate.dao;

import com.linksteady.operate.config.MyMapper;
import com.linksteady.operate.domain.CouponInfo;
import com.linksteady.operate.domain.SmsTemplate;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface CouponMapper extends MyMapper<CouponInfo> {

    List<CouponInfo> getList(@Param("startRow") int startRow, @Param("endRow") int endRow);

    int getTotalCount();

    List<Integer> getCouponIdsByGroupId(String groupId);

    void deleteByGroupId(String groupId);

    void insertByGroupId(String groupId, List<String> couponIds);
}