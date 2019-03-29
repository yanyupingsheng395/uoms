package com.linksteady.operate.service;

import com.linksteady.operate.domain.KeyPointMonth;
import com.linksteady.operate.domain.KeyPointYear;
import com.linksteady.operate.vo.ReasonVO;

import java.util.List;
import java.util.Map;

public interface ReasonService {

     List<Map<String,Object>> getReasonList(int startRow,int endRow);

     int getReasonTotalCount();

     int saveReasonData(ReasonVO reasonVO,String curuser,int primaryKey);

     int getReasonPrimaryKey();
}
