package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.Ztree;
import com.linksteady.operate.dao.*;
import com.linksteady.operate.domain.InsightGrowthPath;
import com.linksteady.operate.domain.InsightImportSpu;
import com.linksteady.operate.domain.InsightUserCnt;
import com.linksteady.operate.service.InsightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @Autowired
    private InsightMapper insightMapper;

    @Override
    public List<InsightUserCnt> findUserCntList(String dateRange) {
        if (dateRange.isEmpty()) {
            dateRange = "1";
        }
        return insightUserCntMapper.findUserCntList(dateRange);
    }

    @Override
    public List<InsightGrowthPath> findGrowthPathList(int start, int end, String sortColumn, String sortOrder) {
        StringBuilder orderSql = new StringBuilder();
        if (null != sortColumn) {
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
                    orderSql.append("order by cops_value desc");
                    break;
            }
        } else {
            orderSql.append("order by cops_value desc");
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
     *
     * @return
     */
    @Override
    public Map<String, Object> getSpuList(String dateRange) {
        if (dateRange.isEmpty()) {
            dateRange = "1";
        }
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> spuList = sankeyMapper.getSpuList(dateRange);

        // 获取node节点数据
        List<String> sourceNames = spuList.stream().map(x -> x.get("SOURCE_NAME").toString()).collect(Collectors.toList());
        List<String> targetNames = spuList.stream().map(x -> x.get("TARGET_NAME").toString()).collect(Collectors.toList());
        sourceNames.addAll(targetNames);
        List<String> nodeNames = sourceNames.stream().distinct().collect(Collectors.toList());
        List<Map<String, Object>> nodeNameArray = Lists.newArrayList();
        nodeNames.stream().forEach(x -> {
            Map<String, Object> nodeNameObject = Maps.newHashMap();
            nodeNameObject.put("name", x);
            nodeNameArray.add(nodeNameObject);
        });
        data.put("nodes", nodeNameArray);

        // 获取link节点数据
        List<Map<String, Object>> linkArray = Lists.newArrayList();
        spuList.stream().forEach(x -> {
            Map<String, Object> linkObject = Maps.newHashMap();
            linkObject.put("source", nodeNames.indexOf(x.get("SOURCE_NAME").toString()));
            linkObject.put("target", nodeNames.indexOf(x.get("TARGET_NAME").toString()));
            linkObject.put("value", ((BigDecimal) x.get("USER_CNT")).longValue());
            linkArray.add(linkObject);
        });
        data.put("links", linkArray);
        return data;
    }

    @Override
    public List<Ztree> getSpuTree() {
        List<Ztree> zTreeList = insightMapper.getSpuTree();
        zTreeList.stream().forEach(x -> {
            x.setOpen(false);
            x.setPId("0");
            x.setIsParent(true);
        });
        return zTreeList;
    }

    @Override
    public List<Ztree> getProductTree(String spuWid) {
        List<Ztree> ztrees = insightMapper.getProductTree(spuWid);
        ztrees.stream().forEach(x -> {
            x.setPId(spuWid);
            x.setIsParent(false);
            x.setOpen(false);
        });
        return ztrees;
    }

    /**
     * 留存率随购买次数变化
     * @param type
     * @param id
     * @param period
     * @return
     */
    @Override
    public Map<String, Object> retentionInPurchaseTimes(String type, String id, String period) {
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> dataList = insightMapper.retentionInPurchaseTimes(type, id, 0 - Integer.valueOf(period));
        List<String> xdata = dataList.stream().map(x -> String.valueOf(x.get("SPU_RN"))).collect(Collectors.toList());
        List<String> ydata = dataList.stream().map(x -> String.valueOf(x.get("LEAVE_RATE") == null ? "0" : x.get("LEAVE_RATE"))).collect(Collectors.toList());
        result.put("xdata", xdata);
        result.put("ydata", ydata);
        return result;
    }

    /**
     * 留存率变化率随购买次数变化
     * @param type
     * @param id
     * @param period
     * @return
     */
    @Override
    public Map<String, Object> retentionChangeRateInPurchaseTimes(String type, String id, String period) {
        Map<String, Object> result = Maps.newHashMap();
        DecimalFormat df = new DecimalFormat("#.##");
        List<Map<String, Object>> dataList = insightMapper.retentionInPurchaseTimes(type, id, 0 - Integer.valueOf(period));
        List<String> xdata = dataList.stream().map(x -> String.valueOf(x.get("SPU_RN"))).collect(Collectors.toList());
        List<String> ydata = dataList.stream().map(x -> String.valueOf(x.get("LEAVE_RATE") == null ? "0" : x.get("LEAVE_RATE"))).collect(Collectors.toList());
        List<String> newXdata = Lists.newArrayList();
        List<String> newYdata = Lists.newArrayList();
        if(ydata.size() > 1) {
            for (int i = 0; i < ydata.size() - 1; i++) {
                String changeRate;
                if(ydata.get(i).equalsIgnoreCase("0")) {
                    changeRate = "";
                }else {
                    changeRate = df.format((Double.valueOf(ydata.get(i+1)) - Double.valueOf(ydata.get(i)))/Double.valueOf(ydata.get(i)));
                }
                newYdata.add(changeRate);
                newXdata.add(xdata.get(i+1));
            }
        }
        result.put("xdata", newXdata);
        result.put("ydata", newYdata);
        return result;
    }

    /**
     * 件单价随购买次数变化
     * @param type
     * @param id
     * @param period
     * @return
     */
    @Override
    public Map<String, Object> unitPriceInPurchaseTimes(String type, String id, String period) {
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> dataList = insightMapper.unitPriceInPurchaseTimes(type, id, period);
        List<String> xdata = dataList.stream().map(x -> String.valueOf(x.get("PURCH_TIMES"))).collect(Collectors.toList());
        List<String> ydata = dataList.stream().map(x -> String.valueOf(x.get("UPRICE") == null ? "0" : x.get("UPRICE"))).collect(Collectors.toList());
        result.put("xdata", xdata);
        result.put("ydata", ydata);
        return result;
    }

    /**
     * 连带率随购买次数变化
     * @param type
     * @param id
     * @param period
     * @return
     */
    @Override
    public Map<String, Object> joinRateInPurchaseTimes(String type, String id, String period) {
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> dataList = insightMapper.joinRateInPurchaseTimes(type, id, period);
        List<String> xdata = dataList.stream().map(x -> String.valueOf(x.get("PURCH_TIMES"))).collect(Collectors.toList());
        List<String> ydata = dataList.stream().map(x -> String.valueOf(x.get("JOINT") == null ? "0" : x.get("JOINT"))).collect(Collectors.toList());
        result.put("xdata", xdata);
        result.put("ydata", ydata);
        return result;
    }

    /**
     * 品类种数随购买次数变化
     * @param type
     * @param id
     * @param period
     * @return
     */
    @Override
    public Map<String, Object> categoryInPurchaseTimes(String type, String id, String period) {
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> dataList = insightMapper.categoryInPurchaseTimes(type, id, period);
        List<String> xdata = dataList.stream().map(x -> String.valueOf(x.get("PURCH_TIMES"))).collect(Collectors.toList());
        List<String> ydata = dataList.stream().map(x -> String.valueOf(x.get("AVG_CATE_NUM") == null ? "0" : x.get("AVG_CATE_NUM"))).collect(Collectors.toList());
        result.put("xdata", xdata);
        result.put("ydata", ydata);
        return result;
    }

    /**
     * 时间间隔随购买次数变化
     * @param type
     * @param id
     * @param period
     * @return
     */
    @Override
    public Map<String, Object> periodInPurchaseTimes(String type, String id, String period) {
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> dataList = insightMapper.periodInPurchaseTimes(type, id, period);
        List<String> xdata = dataList.stream().map(x -> String.valueOf(x.get("PURCH_TIMES"))).collect(Collectors.toList());
        List<String> ydata = dataList.stream().map(x -> String.valueOf(x.get("AVG_PUR_GAP") == null ? "0" : x.get("AVG_PUR_GAP"))).collect(Collectors.toList());
        result.put("xdata", xdata);
        result.put("ydata", ydata);
        return result;
    }
}
