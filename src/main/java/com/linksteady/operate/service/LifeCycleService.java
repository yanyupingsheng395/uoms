package com.linksteady.operate.service;

import com.linksteady.operate.domain.LcSpuInfo;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface LifeCycleService {

     List<LcSpuInfo> getSpuList(String startDt,String endDt);



}
