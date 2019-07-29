package com.linksteady.operate.service;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-07-26
 */
public interface FitDataService {

    List<Double> generateFittingData(String spuId, List<Integer> purchTimes, String type);
}
