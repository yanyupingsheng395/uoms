package com.linksteady.operate.service;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-12-02
 */
public interface SankeyService {

    Map<String, Object> getSpuList();

    List<String> getSunIdList(String id);
}
