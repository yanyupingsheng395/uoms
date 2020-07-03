package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.OrderSeries;

import java.util.List;
import java.util.Map;

/**
 * @author huang
 * @date 2020/7/3
 */
public interface OrderSeriesMapper {

    /**
     * 获取用户购买时序数据
     * @return
     */
    List<OrderSeries> getOrderData(String userId);

}
