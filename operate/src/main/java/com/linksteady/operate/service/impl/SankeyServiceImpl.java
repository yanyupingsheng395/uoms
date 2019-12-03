package com.linksteady.operate.service.impl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.operate.dao.SankeyMapper;
import com.linksteady.operate.service.SankeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * @author hxcao
 * @date 2019-12-02
 */
@Service
public class SankeyServiceImpl implements SankeyService {

    @Autowired
    private SankeyMapper sankeyMapper;

    @Override
    public Map<String, Object> getSpuList() {
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> spuList = sankeyMapper.getSpuList();

        // 获取node节点数据
        List<String> sourceNames = spuList.stream().map(x->x.get("SOURCE_NAME").toString()).collect(Collectors.toList());
        List<String> targetNames = spuList.stream().map(x->x.get("TARGET_NAME").toString()).collect(Collectors.toList());
        sourceNames.addAll(targetNames);
        List<String> nodeNames = sourceNames.stream().distinct().collect(Collectors.toList());
        List<Map<String, Object>> nodeNameArray = Lists.newArrayList();
        nodeNames.stream().forEach(x->{
            Map<String, Object> nodeNameObject = Maps.newHashMap();
            nodeNameObject.put("name", x);
            nodeNameArray.add(nodeNameObject);
        });
        data.put("nodes", nodeNameArray);

        // 获取link节点数据
        List<Map<String, Object>> linkArray = Lists.newArrayList();
        spuList.stream().forEach(x->{
            Map<String, Object> linkObject = Maps.newHashMap();
            linkObject.put("source", nodeNames.indexOf(x.get("SOURCE_NAME").toString()));
            linkObject.put("target", nodeNames.indexOf(x.get("TARGET_NAME").toString()));
            linkObject.put("value", ((BigDecimal)x.get("T_CNT")).longValue());
            linkArray.add(linkObject);
        });
        data.put("links", linkArray);
        return data;
    }
}
