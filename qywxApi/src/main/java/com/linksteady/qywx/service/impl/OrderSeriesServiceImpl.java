package com.linksteady.qywx.service.impl;

import com.linksteady.qywx.dao.OrderSeriesMapper;
import com.linksteady.qywx.domain.OrderSeries;
import com.linksteady.qywx.service.OrderSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrderSeriesServiceImpl implements OrderSeriesService {

    @Autowired
    OrderSeriesMapper orderSeriesMapper;

    @Override
    public List<OrderSeries> getOrderData(String userId) {
        return orderSeriesMapper.getOrderData(userId);
    }
}
