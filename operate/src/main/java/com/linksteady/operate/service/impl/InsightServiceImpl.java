package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.Ztree;
import com.linksteady.operate.dao.*;
import com.linksteady.operate.domain.InsightGrowthPath;
import com.linksteady.operate.domain.InsightImportSpu;
import com.linksteady.operate.domain.InsightUserCnt;
import com.linksteady.operate.service.InsightService;
import org.apache.commons.lang3.StringUtils;
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
    public List<InsightGrowthPath> findGrowthPathList(int start, int end, String sortColumn, String sortOrder, String dateRange) {
        StringBuilder orderSql = new StringBuilder();
        if (null != sortColumn) {
            switch (sortColumn) {
                case "copsValue":
                    orderSql.append("order by cops_value " + sortOrder + ", spu_path " + sortOrder);
                    break;
                case "incomeValue":
                    orderSql.append("order by income_value " + sortOrder + ", spu_path " + sortOrder);
                    break;
                case "stepValue":
                    orderSql.append("order by step_value " + sortOrder + ", spu_path " + sortOrder);
                    break;
                case "universValue":
                    orderSql.append("order by univers_value " + sortOrder + ", spu_path " + sortOrder);
                    break;
                default:
                    orderSql.append("order by cops_value desc, spu_path desc");
                    break;
            }
        } else {
            orderSql.append("order by cops_value desc, spu_path desc");
        }
        return insightGrowthPathMapper.findGrowthPathList(start, end, orderSql.toString(), dateRange);
    }

    @Override
    public int findGrowthPathListCount(String dateRange) {
        return insightGrowthPathMapper.findGrowthPathListCount(dateRange);
    }

    @Override
    public int findImportSpuListCount(String spuName, String purchOrder, String dateRange) {
        return insightImportSpuMapper.findImportSpuListCount(spuName, purchOrder, dateRange);
    }

    @Override
    public List<InsightImportSpu> findImportSpuList(int start, int end, String spuName, String purchOrder, String dateRange, String sortColumn, String sortOrder) {
        StringBuilder orderSql = new StringBuilder();
        if (null != sortColumn) {
            switch (sortColumn) {
                case "contributeRate":
                    orderSql.append("order by CONTRIBUTE_RATE " + sortOrder + ", SPU_ID " + sortOrder);
                    break;
                case "nextPurchProbal":
                    orderSql.append("order by NEXT_PURCH_PROBAL " + sortOrder + ", SPU_ID " + sortOrder);
                    break;
                case "sameSpuProbal":
                    orderSql.append("order by SAME_SPU_PROBAL " + sortOrder + ", SPU_ID " + sortOrder);
                    break;
                case "otherSpuProbal":
                    orderSql.append("order by OTHER_SPU_PROBAL " + sortOrder + ", SPU_ID " + sortOrder);
                    break;
            }
        }
        return insightImportSpuMapper.findImportSpuList(start, end, spuName, purchOrder, dateRange, orderSql.toString());
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
        List<String> nameLists = nodeList.stream().map(x -> x.get("TARGET_NAME").toString()).collect(Collectors.toList());
        nodeList.stream().forEach(x -> {
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
     *
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
     *
     * @param type
     * @param id
     * @param period
     * @return
     */
    @Override
    public Map<String, Object> retentionChangeRateInPurchaseTimes(String type, String id, String period) {
        Map<String, Object> result = Maps.newHashMap();
        DecimalFormat df = new DecimalFormat("#.##");
        List<Map<String, Object>> dataList = insightMapper.retentionInPurchaseTimes(type, id, 0 - Integer.parseInt(period));
        List<String> xdata = dataList.stream().map(x -> String.valueOf(x.get("SPU_RN"))).collect(Collectors.toList());
        List<String> ydata = dataList.stream().map(x -> String.valueOf(x.get("LEAVE_RATE") == null ? "0" : x.get("LEAVE_RATE"))).collect(Collectors.toList());
        List<String> newXdata = Lists.newArrayList();
        List<String> newYdata = Lists.newArrayList();
        if (ydata.size() > 1) {
            for (int i = 0; i < ydata.size() - 1; i++) {
                String changeRate;
                if (ydata.get(i).equalsIgnoreCase("0")) {
                    changeRate = "";
                } else {
                    changeRate = df.format(((Double.parseDouble(ydata.get(i + 1)) - Double.parseDouble(ydata.get(i))) / Double.parseDouble(ydata.get(i))) * 100);
                }
                newYdata.add(changeRate);
                newXdata.add(xdata.get(i + 1));
            }
        }
        result.put("xdata", newXdata);
        result.put("ydata", newYdata);
        return result;
    }

    /**
     * 获取下次转化商品的转化率的top3
     *
     * @param purchOrder
     * @return
     */
    @Override
    public Map<String, Object> getSpuConvertRateNodes(String id, String type, String purchOrder) {
        Map<String, Object> resultMap = Maps.newHashMap();
        List<Map<String, Object>> dataList = insightMapper.getSpuConvertRateProducts(id, type, purchOrder);
        if (dataList.size() == 0) {
            return null;
        }
        // 如果是spu 则需要手动将节点去重
        if ("product".equalsIgnoreCase(type)) {
            // 封装data
            List<Map<String, Object>> data = Lists.newArrayList();
            dataList.forEach(x -> {
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
            dataList.forEach(x -> {
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
        if ("spu".equalsIgnoreCase(type)) {
            final Map<Object, List<Map<String, Object>>> nodeList = dataList.stream().collect(Collectors.groupingBy(x -> Objects.requireNonNull(x.get("SOURCE"))));
            List<Map<String, Object>> categories = Lists.newArrayList();
            List<Map<String, Object>> links = Lists.newArrayList();
            List<Map<String, Object>> data = Lists.newArrayList();
            nodeList.entrySet().forEach(node -> {
                List<Map<String, Object>> eachListMap = node.getValue();
                eachListMap.forEach(x -> {
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
                eachListMap.forEach(x -> {
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

    @Override
    public List<Map<String, Object>> findSpuByPurchOrder(String purchOrder) {
        return insightMapper.findSpuByPurchOrder(purchOrder);
    }

    @Override
    public Map<String, Object> getSpuRelation(String spuId, String purchOrder) {
        Map<String, Object> result = Maps.newHashMap();
        List<String> xdata = Lists.newArrayList();
        List<String> ebpProductIdList = Lists.newArrayList();
        // 实际数据
        List<Integer> ydataActual = Lists.newArrayList();
        // 辅助数据
        List<Integer> ydataReduce = Lists.newArrayList();
        List<Map<String, Object>> mapList = insightMapper.getSpuRelation(spuId, purchOrder);
        if (!mapList.isEmpty()) {
            String spuWid = (mapList.stream().findFirst().get().get("SPU_WID")).toString();
            String spuName = (mapList.stream().findFirst().get().get("SPU_NAME")).toString();
            Integer spuUserCnt = ((BigDecimal) mapList.stream().findFirst().get().get("SPU_CNT")).intValue();
            xdata.add(spuName);
            ebpProductIdList.add(spuWid);
            ydataActual.add(spuUserCnt);
            mapList.stream().forEach(x -> {
                xdata.add(x.get("EBP_PRODUCT_NAME").toString());
                ebpProductIdList.add(x.get("EBP_PRODUCT_ID").toString());
                ydataActual.add(Integer.parseInt(x.get("PRODUCT_CNT").toString()));
            });
            for (int i = 0; i < ydataActual.size(); i++) {
                int tmp = 0;
                if (i <= 1) {
                    tmp = spuUserCnt - ydataActual.get(i);
                } else {
                    tmp = ydataReduce.get(i - 1) - ydataActual.get(i);
                }
                ydataReduce.add(tmp);
            }
            Map<String, Object> convertMap = getProductConvertRate(mapList.stream().findFirst().get().get("EBP_PRODUCT_ID").toString(), spuId, purchOrder);
            result.put("xdata2", convertMap.get("xdata"));
            result.put("ydata2", convertMap.get("ydata"));
            result.put("nextProductId", convertMap.get("nextProductId"));
        }
//        final int i = xdata.indexOf("其他");
//        if(i > -1) {
//            final String tmp1 = xdata.get(i);
//            xdata.remove(i);
//            xdata.add(tmp1);
//
//            final Integer tmp2 = ydataActual.get(i);
//            ydataActual.remove(i);
//            ydataActual.add(tmp2);
//
//            final Integer tmp3 = ydataReduce.get(i);
//            ydataReduce.remove(i);
//            ydataReduce.add(tmp3);
//
//            final String tmp4 = ebpProductIdList.get(i);
//            ebpProductIdList.remove(i);
//            ebpProductIdList.add(tmp4);
//        }

        result.put("xdata1", xdata);
        result.put("ydataActual", ydataActual);
        result.put("ydataReduce", ydataReduce);
        // 获取productId 和 nextProductId
        result.put("productId", ebpProductIdList);
        return result;
    }

    /**
     * 获取第一个用户数大的product的转化商品
     *
     * @return
     */
    @Override
    public Map<String, Object> getProductConvertRate(String productId, String spuId, String purchOrder) {
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> productConvertRate = insightMapper.getProductConvertRate(productId, spuId, purchOrder);
        List<String> xdata = productConvertRate.stream().map(x -> x.get("EBP_PRODUCT_NAME").toString()).collect(Collectors.toList());
        List<String> nextEbpProductIdList = productConvertRate.stream().map(x -> x.get("NEXT_EBP_PRODUCT_ID").toString()).collect(Collectors.toList());
        List<String> ydata = productConvertRate.stream().map(x -> x.get("CONVERT_RATE").toString()).collect(Collectors.toList());

        final int i = xdata.indexOf("其他");
        if (i > -1) {
            final String tmp1 = xdata.get(i);
            xdata.remove(i);
            xdata.add(tmp1);

            final String tmp2 = ydata.get(i);
            ydata.remove(i);
            ydata.add(tmp2);

            final String tmp3 = nextEbpProductIdList.get(i);
            nextEbpProductIdList.remove(i);
            nextEbpProductIdList.add(tmp3);
        }
        result.put("xdata", xdata);
        result.put("ydata", ydata);
        result.put("nextProductId", nextEbpProductIdList);
        return result;
    }

    @Override
    public List<Map<String, Object>> getUserGrowthPath(String spuId, String purchOrder, String ebpProductId, String nextEbpProductId) {
        List<Map<String, Object>> dataList = Lists.newArrayList();
        if (StringUtils.isNotBlank("ebpProductId") && StringUtils.isNotBlank("nextEbpProductId")) {
            dataList = insightMapper.getUserGrowthPathWithProduct(spuId, purchOrder, ebpProductId, nextEbpProductId);
        }
        if (dataList.isEmpty()) {
            dataList = insightMapper.getUserGrowthPathWithSpu(spuId, purchOrder);
        }
        return dataList;
    }

    @Override
    public List<Map<String, Object>> getGrowthUser(String spuId, String purchOrder, String ebpProductId, String nextEbpProductId, int start, int end) {
        return insightMapper.getGrowthUser(spuId, purchOrder, ebpProductId, nextEbpProductId, start, end);
    }

    @Override
    public int getGrowthUserCount(String spuId, String purchOrder, String ebpProductId, String nextEbpProductId) {
        return insightMapper.getGrowthUserCount(spuId, purchOrder, ebpProductId, nextEbpProductId);
    }

    @Override
    public List<Map<String, Object>> getPathSpu() {
        return insightMapper.getPathSpu();
    }

    @Override
    public List<String> getPathPurchOrder(String spuId) {
        List<String> pathPurchOrder = insightMapper.getPathPurchOrder(spuId);
        if (!pathPurchOrder.isEmpty()) {
            pathPurchOrder.remove(pathPurchOrder.size() - 1);
        }
        return pathPurchOrder;
    }

    /**
     * 件单价随购买次数变化
     *
     * @param type
     * @param id
     * @param period
     * @return
     */
    @Override
    public Map<String, Object> unitPriceInPurchaseTimes(String type, String id, String period) {
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> dataList = insightMapper.unitPriceInPurchaseTimes(type, id, 0 - Integer.valueOf(period));
        List<String> xdata = dataList.stream().map(x -> String.valueOf(x.get("PURCH_TIMES"))).collect(Collectors.toList());
        List<String> ydata = dataList.stream().map(x -> String.valueOf(x.get("UPRICE") == null ? "0" : x.get("UPRICE"))).collect(Collectors.toList());
        result.put("xdata", xdata);
        result.put("ydata", ydata);
        return result;
    }

    /**
     * 连带率随购买次数变化
     *
     * @param type
     * @param id
     * @param period
     * @return
     */
    @Override
    public Map<String, Object> joinRateInPurchaseTimes(String type, String id, String period) {
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> dataList = insightMapper.joinRateInPurchaseTimes(type, id, 0 - Integer.valueOf(period));
        List<String> xdata = dataList.stream().map(x -> String.valueOf(x.get("PURCH_TIMES"))).collect(Collectors.toList());
        List<String> ydata = dataList.stream().map(x -> String.valueOf(x.get("JOINT") == null ? "0" : x.get("JOINT"))).collect(Collectors.toList());
        result.put("xdata", xdata);
        result.put("ydata", ydata);
        return result;
    }

    /**
     * 品类种数随购买次数变化
     *
     * @param type
     * @param id
     * @param period
     * @return
     */
    @Override
    public Map<String, Object> categoryInPurchaseTimes(String type, String id, String period) {
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> dataList = insightMapper.categoryInPurchaseTimes(type, id, 0 - Integer.valueOf(period));
        List<String> xdata = dataList.stream().map(x -> String.valueOf(x.get("PURCH_TIMES"))).collect(Collectors.toList());
        List<String> ydata = dataList.stream().map(x -> String.valueOf(x.get("AVG_CATE_NUM") == null ? "0" : x.get("AVG_CATE_NUM"))).collect(Collectors.toList());
        result.put("xdata", xdata);
        result.put("ydata", ydata);
        return result;
    }

    /**
     * 时间间隔随购买次数变化
     *
     * @param type
     * @param id
     * @param period
     * @return
     */
    @Override
    public Map<String, Object> periodInPurchaseTimes(String type, String id, String period) {
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> dataList = insightMapper.periodInPurchaseTimes(type, id, 0 - Integer.valueOf(period));
        List<String> xdata = dataList.stream().map(x -> String.valueOf(x.get("PURCH_TIMES"))).collect(Collectors.toList());
        List<String> ydata = dataList.stream().map(x -> String.valueOf(x.get("AVG_PUR_GAP") == null ? "0" : x.get("AVG_PUR_GAP"))).collect(Collectors.toList());
        result.put("xdata", xdata);
        result.put("ydata", ydata);
        return result;
    }
}