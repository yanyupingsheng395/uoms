package com.linksteady.operate.service;
import com.linksteady.operate.domain.TargetList;
import java.util.List;
import java.util.Map;

/**
 * Created by hxcao on 2019-05-22
 */
public interface TargetListService {
    void save(TargetList target);
    List<Map<String, Object>> getPageList(int startRow, int endRow);
    int getTotalCount();
    Map<String, Object> getDataById(Long id);
    List<TargetList> getTargetList();
    Map<String, Object> getMonitorVal(String targetId);
}
