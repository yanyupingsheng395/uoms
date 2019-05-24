package com.linksteady.operate.dao;

import com.linksteady.operate.domain.LcSpuInfo;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface LifeCycleMapper {

    List<LcSpuInfo> getSpuList(@Param("startDt") String startDt,@Param("endDt") String endDt);

}