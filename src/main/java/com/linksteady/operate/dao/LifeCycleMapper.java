package com.linksteady.operate.dao;

import com.linksteady.operate.domain.LcSpuInfo;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface LifeCycleMapper {

    List<LcSpuInfo> getSpuList(@Param("startDt") String startDt,@Param("endDt") String endDt);

    List<LcSpuInfo> getSpuListWithUserCount(@Param("startDt") String startDt,@Param("endDt") String endDt);

    List<LcSpuInfo> getSpuListWithPoCount(@Param("startDt") String startDt,@Param("endDt") String endDt);

    List<LcSpuInfo> getSpuListWithJoinRate(@Param("startDt") String startDt,@Param("endDt") String endDt);

    List<LcSpuInfo> getSpuListWithSprice(@Param("startDt") String startDt,@Param("endDt") String endDt);

}