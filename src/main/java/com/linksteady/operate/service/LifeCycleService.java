package com.linksteady.operate.service;

import com.linksteady.operate.domain.LcSpuInfo;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface LifeCycleService {

     List<LcSpuInfo> getSpuList(String startDt,String endDt);

     List<Double> getAllGmvByDay(String startDt,String endDt);

     List<Double> getSpuGmvByDay(int spu_wid,String startDt,String endDt);

     void updateRelate(int spuWid,double relate);



}
