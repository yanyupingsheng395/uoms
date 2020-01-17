package com.linksteady.operate.service;

import com.linksteady.operate.domain.CouponInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by hxcao on 2019-04-29
 */
public interface ConfigService {

    List<Map<String, String>> selectPathActive();

    List<Map<String, String>> selectUserValue();

    List<Map<String, String>> selectLifeCycle();

    /**
     * 加载其它通用配置
     */
    List<Map<String, String>> selectCommonConfig();
}
