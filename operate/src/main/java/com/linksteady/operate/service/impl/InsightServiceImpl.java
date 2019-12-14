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
import java.util.*;
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

    @Autowired
    private InsightMapper insightMapper;

    @Override
    public List<InsightUserCnt> findUserCntList(String dateRange) {
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
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> nodeList = sankeyMapper.getNodeInfo(dateRange);
        // 获取node节点基本数据
        List<Map<String, Object>> nodeNameArray = Lists.newArrayList();
        List<String> nameLists = nodeList.stream().map(x->x.get("TARGET_NAME").toString()).collect(Collectors.toList());
        nodeList.stream().forEach(x->{
            Map<String, Object> node = Maps.newHashMap();
            node.put("id", String.valueOf(nodeList.indexOf(x)));
            node.put("name", x.get("TARGET_NAME"));
            node.put("cUserCnt", x.get("C_USER_CNT").toString()); // 当日用户数量
            node.put("bUserCnt", x.get("B_USER_CNT").toString()); // 30日用户数量
            node.put("cUserPercent", x.get("C_RATE").toString()); // 当日用户数量占比
            node.put("bUserPercent", x.get("B_RATE").toString()); // 30日用户数量占比
            nodeNameArray.add(node);
        });
        data.put("nodes", nodeNameArray);
        // 获取link节点数据
        List<Map<String, Object>> linkList = sankeyMapper.getLinkInfo(dateRange);
        List<Map<String, Object>> linkArray = Lists.newArrayList();
        linkList.stream().forEach(x -> {
            Map<String, Object> linkObject = Maps.newHashMap();
            linkObject.put("source", nameLists.indexOf(x.get("SOURCE_NAME").toString()));
            linkObject.put("target", nameLists.indexOf(x.get("TARGET_NAME").toString()));
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
                    changeRate = df.format(((Double.valueOf(ydata.get(i+1)) - Double.valueOf(ydata.get(i)))/Double.valueOf(ydata.get(i))) * 100);
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
     * 获取下次转化商品的转化率的top3
     * @param purchOrder
     * @return
     */
    @Override
    public Map<String, Object> getSpuConvertRateNodes(String id, String type, String purchOrder) {
        Map<String, Object> resultMap = Maps.newHashMap();
        List<Map<String, Object>> dataList = insightMapper.getSpuConvertRateProducts(id, type, purchOrder);
        if(dataList.size() == 0 ) {
            return null;
        }
        // 如果是spu 则需要手动将节点去重
        if("product".equalsIgnoreCase(type)) {
            // 封装data
            List<Map<String, Object>> data = Lists.newArrayList();
            dataList.forEach(x->{
                Map<String, Object> dataMap = Maps.newHashMap();
                dataMap.put("name", x.get("TARGET"));
                dataMap.put("symbolSize", 30);
                dataMap.put("category", x.get("SOURCE"));
                dataMap.put("value", x.get("VALUE"));
                data.add(dataMap);
            });
            Map<String, Object> dataMap = Maps.newHashMap();
            dataMap.put("name", dataList.get(0).get("SOURCE"));
            dataMap.put("symbolSize", 50);
            Map<String, Object> label = Maps.newHashMap();
            label.put("show", true);
            dataMap.put("label", label);
            data.add(dataMap);

            resultMap.put("data", data);
            resultMap.put("name", dataList.get(0).get("SOURCE"));

            // 封装links
            List<Map<String, Object>> links = Lists.newArrayList();
            dataList.forEach(x->{
                Map<String, Object> linkMap = Maps.newHashMap();
                linkMap.put("source", x.get("SOURCE"));
                linkMap.put("target", x.get("TARGET"));
                links.add(linkMap);
            });

            resultMap.put("links", links);

            // 封装categories
            List<Map<String, Object>> categories = Lists.newArrayList();
            Map<String, Object> categoriesMap = Maps.newHashMap();
            categoriesMap.put("name", dataList.get(0).get("SOURCE"));
            categories.add(categoriesMap);
            resultMap.put("categories", categories);
        }
        if("spu".equalsIgnoreCase(type)) {
            final Map<Object, List<Map<String, Object>>> nodeList = dataList.stream().collect(Collectors.groupingBy(x -> x.get("SOURCE")));
            List<Map<String, Object>> categories = Lists.newArrayList();
            List<Map<String, Object>> links = Lists.newArrayList();
            List<Map<String, Object>> data = Lists.newArrayList();
            nodeList.entrySet().forEach(node->{
                List<Map<String, Object>> eachListMap = node.getValue();
                eachListMap.forEach(x->{
                    Map<String, Object> dataMap = Maps.newHashMap();
                    dataMap.put("name", x.get("TARGET"));
                    dataMap.put("symbolSize", 30);
                    dataMap.put("category", x.get("SOURCE"));
                    dataMap.put("value", x.get("VALUE"));
                    data.add(dataMap);
                });
                Map<String, Object> dataMap = Maps.newHashMap();
                dataMap.put("name", node.getKey());
                dataMap.put("symbolSize", 50);
                dataMap.put("category", node.getKey());
                data.add(dataMap);

                resultMap.put("data", data);

                // 封装links
                eachListMap.forEach(x->{
                    Map<String, Object> linkMap = Maps.newHashMap();
                    linkMap.put("source", x.get("SOURCE"));
                    linkMap.put("target", x.get("TARGET"));
                    links.add(linkMap);
                });
                resultMap.put("links", links);

                // 封装categories
                Map<String, Object> categoriesMap = Maps.newHashMap();
                categoriesMap.put("name", node.getKey());
                categories.add(categoriesMap);

            });
            resultMap.put("categories", categories);
        }
        return resultMap;
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
