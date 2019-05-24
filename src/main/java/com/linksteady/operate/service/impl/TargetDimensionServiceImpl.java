package com.linksteady.operate.service.impl;
import com.linksteady.operate.dao.TargetDimensionMapper;
import com.linksteady.operate.domain.TargetDimension;
import com.linksteady.operate.service.TargetDimensionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by hxcao on 2019-05-22
 */
@Service
public class TargetDimensionServiceImpl implements TargetDimensionService {

    @Autowired
    private TargetDimensionMapper targetDimensionMapper;

    @Override
    public List<Map<String, Object>> getDimensionsById(String id) {
        return targetDimensionMapper.getListByTgtId(Long.valueOf(id));
    }

    @Override
    public List<Map<String, Object>> getDataList(List<Map<String, Object>> dataList) {
        dataList.stream().forEach(x-> {
            Long id = Long.valueOf(x.get("ID").toString());
            List<Map<String, Object>> tmpList = targetDimensionMapper.getListByTgtId(id);
            String dimension = "";
            for (Map<String, Object> map: tmpList) {
                dimension += map.get("DIMENSION_NAME") + ":" + map.get("DIMENSION_VAL_NAME") + "|";
            }
            dimension = dimension.substring(0, dimension.lastIndexOf("|"));
            x.put("DIMENSIONS", dimension);
        });
        return dataList;
    }
}
