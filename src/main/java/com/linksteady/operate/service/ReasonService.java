package com.linksteady.operate.service;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.vo.ReasonVO;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface ReasonService {

     List<Reason> getReasonList(int startRow, int endRow);

     int getReasonTotalCount();

     void saveReasonData(ReasonVO reasonVO,String username,int primaryKey);

     int getReasonPrimaryKey();

     void saveReasonDetail(int primaryKey,String[] dims);

     void deleteReasonById(String reasonId);

     void findReasonKpisSnp(String reasonId);

     Map<String,Object> getReasonAllInfoById(String reasonId);

     List<ReasonKpisSnp> getReasonKpisSnp(String reasonId, String templateCode);

     List<ReasonResult> getReasonResultList(String reasonId);

     int  getReasonResultCount(String reasonId,String fcode);

     void deleteReasonResult(String reasonResultId);

     void saveReasonResult(String reasonId,String fcode,String fname,String formula,String business);

     List<ReasonRelMatrix> getReasonResultByCode(String reasonId, String fcode, String rfcode);

     Reason getReasonHeaderInfoById(String reasonId);

     int getResultTraceCount(String reasonResultId);

     void addResultToTrace(String reasonId,String reasonResultId);

     void deleteResultToTrace(String reasonResultId);

     List<ReasonResultTrace> getReasonResultTraceList(String username);


}
