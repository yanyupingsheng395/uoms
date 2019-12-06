package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.operate.dao.InsightGrowthPathMapper;
import com.linksteady.operate.dao.InsightImportSpuMapper;
import com.linksteady.operate.dao.InsightSankeyMapper;
import com.linksteady.operate.dao.InsightUserCntMapper;
import com.linksteady.operate.domain.InsightGrowthPath;
import com.linksteady.operate.domain.InsightImportSpu;
import com.linksteady.operate.domain.InsightUserCnt;
import com.linksteady.operate.service.InsightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2019-12-04
 */
@Service
public class InsightServiceImpl implements InsightService {

    @Autowired
    private InsightUserCntMapper insightUserCntMapper;

    @Autowired
    private InsightGrowthPathMapper insightGrowthPathMapper;

    @Autowired
    private InsightImportSpuMapper insightImportSpuMapper;

    @Autowired
    private InsightSankeyMapper sankeyMapper;

    @Override
    public List<InsightUserCnt> findUserCntList(String dateRange) {
        if(dateRange.isEmpty()) {
            dateRange = "1";
        }
        return insightUserCntMapper.findUserCntList(dateRange);
    }

    @Override
    public List<InsightGrowthPath> findGrowthPathList(int start, int end, String sortColumn, String sortOrder) {
        StringBuilder orderSql = new StringBuilder();
        if(null != sortColumn) {
            switch (sortColumn) {
                case "copsValue":
                    orderSql.append("order by cops_value " + sortOrder);
                    break;
                case "incomeValue":
                    orderSql.append("order by income_value " + sortOrder);
                    break;
                case "stepValue":
                    orderSql.append("order by step_value " + sortOrder);
                    break;
                case "universValue":
                    orderSql.append("order by univers_value " + sortOrder);
                    break;
                default:
                    break;
            }
        }
        return insightGrowthPathMapper.findGrowthPathList(start, end, orderSql.toString());
    }

    @Override
    public int findGrowthPathListCount() {
        return insightGrowthPathMapper.findGrowthPathListCount();
    }

    @Override
    public int findImportSpuListCount(String spuName, String purchOrder) {
        return insightImportSpuMapper.findImportSpuListCount(spuName, purchOrder);
    }

    @Override
    public List<InsightImportSpu> findImportSpuList(int start, int end, String spuName, String purchOrder) {
        return insightImportSpuMapper.findImportSpuList(start, end, spuName, purchOrder);
    }

    @Override
    public InsightGrowthPath getGrowthPathAvgValue() {
        return insightGrowthPathMapper.getGrowthPathAvgValue();
    }

    /**
     * spu桑基图
     * @return
     */
    @Override
    public Map<String, Object> getSpuList(String dateRange) {
        if(dateRange.isEmpty()) {
            dateRange = "1";
        }
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> spuList = sankeyMapper.getSpuList(dateRange);

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
            linkObject.put("value", ((BigDecimal)x.get("USER_CNT")).longValue());
            linkArray.add(linkObject);
        });
        data.put("links", linkArray);
        return data;
    }
}
