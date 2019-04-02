package com.linksteady.operate.service;
import com.linksteady.operate.vo.ReasonVO;

import java.util.List;
import java.util.Map;

public interface ReasonService {

     List<Map<String,Object>> getReasonList(int startRow,int endRow);

     int getReasonTotalCount();

     String saveReasonData(ReasonVO reasonVO,String curuser,int primaryKey);

     int getReasonPrimaryKey();

     void saveReasonDetail(int primaryKey,String[] dims);

     void saveReasonTemplate(int primaryKey,String [] templates);

     void deleteReasonById(String reasonId);

     void updateProgressById(String reasonId,int progress);

     void findReasonKpis(String reasonId);

     Map<String,Object> getReasonInfoById(String reasonId);

     List<Map<String,String>> getRelatedKpiList(String reasonId,String templateCode);

     List<Map<String,Object>> getReasonKpiHistroy(String kpiCode,String templateCode);

     Map<String,String> getReasonRelatedKpi(String reasonId,String kpiCode,String templateCode);
}
