package com.linksteady.operate.service;
import com.linksteady.operate.vo.ReasonVO;

import java.util.List;
import java.util.Map;

public interface LifeCycleService {

     List<Map<String,Object>> getCatList(int startRow, int endRow,String orderColumn,String cateName);

     int getCatTotalCount(String cateName);

}
