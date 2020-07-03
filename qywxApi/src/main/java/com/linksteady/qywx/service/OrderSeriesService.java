package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.OrderSeries;

import java.util.List;
import java.util.Map;

/**
 * @author huang
 * @date 2020/7/3
 */
public interface OrderSeriesService {

    List<OrderSeries>  getOrderData(String userId);

}
