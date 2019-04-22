package com.linksteady.operate.service;
import com.linksteady.operate.domain.ReasonRelMatrix;
import com.linksteady.operate.domain.ReasonResult;
import com.linksteady.operate.vo.ReasonVO;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface ReasonService {

     List<Map<String,Object>> getReasonList(int startRow,int endRow);

     int getReasonTotalCount();

     void saveReasonData(ReasonVO reasonVO,String curuser,int primaryKey);

     int getReasonPrimaryKey();

     void saveReasonDetail(int primaryKey,String[] dims);

     void deleteReasonById(String reasonId);

     void updateProgressById(String reasonId,int progress);

     void findReasonKpisSnp(String reasonId);

     Map<String,Object> getReasonInfoById(String reasonId);

     List<Map<String,Object>> getReasonKpisSnp(String reasonId,String templateCode);

     List<Map<String,Object>> getConcernKpiList(String reasonId);

     int getConcernKpiCount(String reasonId,String templateCode,String reasonKpiCode);

     void addConcernKpi(String reasonId,String templateCode,String reasonKpiCode);

     void deleteConcernKpi(String reasonId, String templateCode, String reasonKpiCode);

     List<ReasonResult> getReasonResultList(String reasonId);

     int  getReasonResultCount(String reasonId,String fcode);

     void deleteReasonResult(String reasonId,String fcode);

     void saveReasonResult(String reasonId,String fcode,String fname,String formula,String business);

     List<ReasonRelMatrix> getReasonResultByCode(String reasonId, String fcode, String rfcode);


}
