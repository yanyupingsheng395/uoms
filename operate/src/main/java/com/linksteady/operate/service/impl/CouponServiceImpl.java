package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.CouponMapper;
import com.linksteady.operate.domain.CouponInfo;
import com.linksteady.operate.service.CouPonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by hxcao on 2019-04-29
 */
@Service
public class CouponServiceImpl implements CouPonService {

    @Autowired
    private CouponMapper couponMapper;

    @Override
    public List<CouponInfo> getList(int startRow, int endRow) {
        return couponMapper.getList(startRow, endRow);
    }

    @Override
    public int getTotalCount() {
        return couponMapper.getTotalCount();
    }

}
