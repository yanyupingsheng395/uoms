package com.linksteady.mdss.service;
import com.linksteady.mdss.domain.*;
import com.linksteady.mdss.vo.ReasonVO;

import java.util.List;
import java.util.Map;

public interface ReasonService {

     List<Reason> getReasonList(int startRow, int endRow, String reasonName);

     int getReasonTotalCount(String reasonName);

     void saveReasonData(ReasonVO reasonVO, String username, int primaryKey);

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
