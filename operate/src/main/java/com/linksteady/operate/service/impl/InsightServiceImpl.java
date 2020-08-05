package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.dao.DictMapper;
import com.linksteady.common.domain.Dict;
import com.linksteady.common.domain.Ztree;
import com.linksteady.common.util.DateUtil;
import com.linksteady.operate.dao.*;
import com.linksteady.operate.domain.InsightGrowthPath;
import com.linksteady.operate.domain.InsightImportSpu;
import com.linksteady.operate.domain.InsightUserCnt;
import com.linksteady.operate.domain.InsightUserEffect;
import com.linksteady.operate.service.InsightService;
import com.linksteady.operate.thrift.ConversionData;
import com.linksteady.operate.thrift.ThriftClient;
import com.linksteady.operate.thrift.RetentionData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2019-12-04
 */
@Slf4j
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

    @Autowired
    private ThriftClient thriftClient;

    @Autowired
    private DictMapper dictMapper;

    /**
     * 由于thrift 中 seqid资源是线程不安全的，所以需要通过加锁的方式来同步调用资源。
     * 否则会报msg.seqid ！= seqid  => badseqid exception的异常信息。
     */
    private ReentrantLock lock = new ReentrantLock();

    @Override
    public List<InsightUserCnt> findUserCntList(Long dateRange) {
        return insightUserCntMapper.findUserCntList(dateRange);
    }

    @Override
    public List<InsightGrowthPath> findGrowthPathList(int limit, int offset, String sortColumn, String sortOrder, String dateRange) {
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
        return insightGrowthPathMapper.findGrowthPathList(limit, offset, orderSql.toString(), dateRange);
    }

    @Override
    public int findGrowthPathListCount(String dateRange) {
        return insightGrowthPathMapper.findGrowthPathListCount(dateRange);
    }

    @Override
    public int findImportSpuListCount(String purchOrder, String dateRange) {
        return insightImportSpuMapper.findImportSpuListCount(purchOrder, dateRange);
    }

    @Override
    public List<InsightImportSpu> findImportSpuList(int limit, int offset, String spuId, String purchOrder, String dateRange, String sortColumn, String sortOrder) {
        StringBuilder orderSql = new StringBuilder();
        if (null != sortColumn) {
            switch (sortColumn) {
                case "contributeRate":
                    orderSql.append("order by case when SPU_ID = '" + spuId + "' then 1 else 0 end desc, CONTRIBUTE_RATE " + sortOrder + ", SPU_ID " + sortOrder);
                    break;
                case "nextPurchProbal":
                    orderSql.append("order by case when SPU_ID = '" + spuId + "' then 1 else 0 end desc, NEXT_PURCH_PROBAL " + sortOrder + ", SPU_ID " + sortOrder);
                    break;
                case "sameSpuProbal":
                    orderSql.append("order by case when SPU_ID = '" + spuId + "' then 1 else 0 end desc, SAME_SPU_PROBAL " + sortOrder + ", SPU_ID " + sortOrder);
                    break;
                case "otherSpuProbal":
                    orderSql.append("order by case when SPU_ID = '" + spuId + "' then 1 else 0 end desc, OTHER_SPU_PROBAL " + sortOrder + ", SPU_ID " + sortOrder);
                    break;
                default:
                    orderSql.append("order by case when SPU_ID = '" + spuId + "' then 1 else 0 end desc, CONTRIBUTE_RATE desc, SPU_ID asc");
                    break;
            }
        } else {
            orderSql.append("order by case when SPU_ID = '" + spuId + "' then 1 else 0 end desc, CONTRIBUTE_RATE desc, SPU_ID asc");
        }
        final List<InsightImportSpu> importSpuList = insightImportSpuMapper.findImportSpuList(limit, offset, purchOrder, dateRange, orderSql.toString(), spuId);
        InsightImportSpu avg = insightImportSpuMapper.findAvgImportSpu(purchOrder, dateRange);
        importSpuList.add(avg);
        return importSpuList;
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
        Map<String, Object> data = new HashMap<>(16);
        List<Map<String, Object>> nodeList = sankeyMapper.getNodeInfo(dateRange);
        // 获取node节点基本数据
        List<Map<String, Object>> nodeNameArray = Lists.newArrayList();
        List<String> nameLists = nodeList.stream().map(x -> x.get("target_name").toString()).collect(Collectors.toList());
        nodeList.stream().forEach(x -> {
            Map<String, Object> node = Maps.newHashMap();
            node.put("id", String.valueOf(nodeList.indexOf(x)));
            node.put("name", x.get("target_name"));
            // 当日用户数量
            node.put("cUserCnt", x.get("c_user_cnt").toString());
            // 30日用户数量
            node.put("bUserCnt", x.get("b_user_cnt").toString());
            // 当日用户数量占比
            node.put("cUserPercent", x.get("c_rate").toString());
            // 30日用户数量占比
            node.put("bUserPercent", x.get("b_rate").toString());
            nodeNameArray.add(node);
        });
        data.put("nodes", nodeNameArray);
        // 获取link节点数据
        List<Map<String, Object>> linkList = sankeyMapper.getLinkInfo(dateRange);
        List<Map<String, Object>> linkArray = Lists.newArrayList();
        linkList.stream().forEach(x -> {
            Map<String, Object> linkObject = Maps.newHashMap();
            linkObject.put("source", nameLists.indexOf(x.get("source_name").toString()));
            linkObject.put("target", nameLists.indexOf(x.get("target_name").toString()));
            linkObject.put("value", x.get("user_cnt"));
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
            x.setPId(0);
            x.setIsParent(true);
        });
        return zTreeList;
    }

    @Override
    public List<Ztree> getProductTree(Long spuWid) {
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
    public Map<String, Object> retentionInPurchaseTimes(String type, Long id, Long period,Long periodStartDt) throws TTransportException {
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> dataList = insightMapper.retentionInPurchaseTimes(type, id, periodStartDt);
        List<String> xdata = dataList.stream().map(x -> String.valueOf(x.get("spu_rn"))).collect(Collectors.toList());
        List<String> ydata = dataList.stream().map(x -> String.valueOf(x.get("leave_rate") == null ? "0" : x.get("leave_rate"))).collect(Collectors.toList());
        result.put("xdata", xdata);
        result.put("ydata", ydata);
        result.put("fdata", getRetentionFitData(type, id, period));
        return result;
    }

    @Override
    public Map<String, Object> retentionInPurchaseTimesOfAll(String spuId) throws TTransportException {
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> dataList = insightMapper.retentionInPurchaseTimesOfAll(spuId);
        List<String> xdata = dataList.stream().map(x -> String.valueOf(x.get("spu_rn"))).collect(Collectors.toList());
        List<String> ydata = dataList.stream().map(x -> String.valueOf(x.get("leave_rate") == null ? "0" : x.get("leave_rate"))).collect(Collectors.toList());
        result.put("xdata", xdata);
        result.put("ydata", ydata);
        return result;
    }

    /**
     * 留存率变化率随购买次数变化
     *
     * @param type
     * @param id
     * @param periodStartDt
     * @return
     */
    @Override
    public Map<String, Object> retentionChangeRateInPurchaseTimes(String type, Long id,Long period,Long periodStartDt) {
        Map<String, Object> result = Maps.newHashMap();
        DecimalFormat df = new DecimalFormat("#.##");
        List<Map<String, Object>> dataList = insightMapper.retentionInPurchaseTimes(type, id, periodStartDt);
        List<String> xdata = dataList.stream().map(x -> String.valueOf(x.get("spu_rn"))).collect(Collectors.toList());
        List<String> ydata = dataList.stream().map(x -> String.valueOf(x.get("leave_rate") == null ? "0" : x.get("leave_rate"))).collect(Collectors.toList());
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
        result.put("fdata", getRetentionChangeFitData(type, id, period));
        return result;
    }


    @Override
    public List<Map<String, Object>> findSpuByPurchOrder(Long purchOrder) {
        return insightMapper.findSpuByPurchOrder(purchOrder);
    }

    @Override
    public Map<String, Object> getSpuRelation(Long spuId, Long purchOrder) {
        Map<String, Object> result = Maps.newHashMap();
        List<String> xdata = Lists.newArrayList();
        List<String> ebpProductIdList = Lists.newArrayList();
        // 实际数据
        List<Integer> ydataActual = Lists.newArrayList();
        // 辅助数据
        List<Integer> ydataReduce = Lists.newArrayList();
        List<Map<String, Object>> mapList = insightMapper.getSpuRelation(spuId, purchOrder);
        if (!mapList.isEmpty()) {
            String spuWid = (mapList.stream().findFirst().get().get("spu_wid")).toString();
            String spuName = (mapList.stream().findFirst().get().get("spu_name")).toString();
            Integer spuUserCnt = ((BigDecimal) mapList.stream().findFirst().get().get("spu_cnt")).intValue();
            xdata.add(spuName);
            ebpProductIdList.add(spuWid);
            ydataActual.add(spuUserCnt);
            mapList.stream().forEach(x -> {
                xdata.add(x.get("ebp_product_name").toString());
                ebpProductIdList.add(x.get("ebp_product_id").toString());
                ydataActual.add(Integer.parseInt(x.get("product_cnt").toString()));
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
            Map<String, Object> convertMap = getProductConvertRate(Long.parseLong(mapList.stream().findFirst().get().get("ebp_product_id").toString()), spuId, purchOrder);
            result.put("xdata2", convertMap.get("xdata"));
            result.put("ydata2", convertMap.get("ydata"));
            result.put("nextProductId", convertMap.get("nextProductId"));
        }

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
    public Map<String, Object> getProductConvertRate(Long productId, Long spuId, Long purchOrder) {
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> productConvertRate = insightMapper.getProductConvertRate(productId, spuId, purchOrder);
        List<String> xdata = productConvertRate.stream().map(x -> x.get("ebp_product_name").toString()).collect(Collectors.toList());
        List<String> nextEbpProductIdList = productConvertRate.stream().map(x -> x.get("next_ebp_product_id").toString()).collect(Collectors.toList());
        List<String> ydata = productConvertRate.stream().map(x -> x.get("convert_rate").toString()).collect(Collectors.toList());

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
    public List<Map<String, Object>> getUserGrowthPath(Long spuId, Long purchOrder, Long ebpProductId, Long nextEbpProductId) {
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
    public List<Map<String, Object>> getGrowthUser(Long spuId, Long purchOrder, Long ebpProductId, Long nextEbpProductId, int limit, int offset) {
        return insightMapper.getGrowthUser(spuId, purchOrder, ebpProductId, nextEbpProductId, limit, offset);
    }

    @Override
    public int getGrowthUserCount(Long spuId, Long purchOrder, Long ebpProductId, Long nextEbpProductId) {
        return insightMapper.getGrowthUserCount(spuId, purchOrder, ebpProductId, nextEbpProductId);
    }

    @Override
    public List<Map<String, Object>> getPathSpu() {
        return insightMapper.getPathSpu();
    }

    @Override
    public List<String> getPathPurchOrder(Long spuId) {
        List<String> pathPurchOrder = insightMapper.getPathPurchOrder(spuId);
        if (!pathPurchOrder.isEmpty()) {
            pathPurchOrder.remove(pathPurchOrder.size() - 1);
        }
        return pathPurchOrder;
    }

    @Override
    public List<String> getRetentionFitData(String type, Long id, Long period) throws TTransportException {
        List<String> retentionFitList = Lists.newArrayList();
        lock.lock();
        try {
            DecimalFormat df = new DecimalFormat("#.##");
            int spu = -1;
            int product = -1;

            if (!thriftClient.isOpend()) {
                thriftClient.open();
            }

            if (type.equalsIgnoreCase("spu")) {
                spu = id.intValue();
            }
            if (type.equalsIgnoreCase("product")) {
                product = id.intValue();
            }
            RetentionData retentionFitData = thriftClient.getInsightService().getRetentionFitData(spu, product, period.intValue());
            List<Double> retentionFit = retentionFitData.getRetentionFit();
            retentionFitList = retentionFit.stream().map(df::format).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取拟合值数据异常", e);
            thriftClient.close();
        } finally {
            lock.unlock();
        }
        return retentionFitList;
    }

    @Override
    public List<String> getRetentionChangeFitData(String type, Long id, Long period) {
        List<String> retentionFitList = Lists.newArrayList();
        lock.lock();
        try {
            DecimalFormat df = new DecimalFormat("#.##");
            int spu = -1;
            int product = -1;

            if (!thriftClient.isOpend()) {
                thriftClient.open();
            }

            if (type.equalsIgnoreCase("spu")) {
                spu =id.intValue();
            }
            if (type.equalsIgnoreCase("product")) {
                product = id.intValue();
            }
            RetentionData retentionFitData = thriftClient.getInsightService().getRetentionFitData(spu, product, period.intValue());
            List<Double> retentionFit = retentionFitData.getRetentionFit();
            retentionFitList = retentionFit.stream().map(df::format).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取拟合值数据异常", e);
            thriftClient.close();
        } finally {
            lock.unlock();
        }
        return retentionFitList;
    }

    @Override
    public Map<String, Object> getConvertRateChart(Long spuId, Long purchOrder, Long ebpProductId, Long nextEbpProductId) {
        DecimalFormat df = new DecimalFormat("#.##");
        Map<String, Object> result = Maps.newHashMap();
        lock.lock();
        try {
            if (!thriftClient.isOpend()) {
                thriftClient.open();
            }
            if (null!=ebpProductId) {
                if (null==nextEbpProductId) {
                    nextEbpProductId = -1L;
                }
                ConversionData conversionData = thriftClient.getInsightService().getConversionData(spuId, purchOrder, ebpProductId, nextEbpProductId);

                result.put("xdata", conversionData.xdata);
                result.put("ydata", conversionData.ydata.stream().map(x -> df.format(x * 100)).collect(Collectors.toList()));
                result.put("zdata", conversionData.zdata.stream().map(x -> df.format(x * 100)).collect(Collectors.toList()));
            } else {
                result.put("xdata", Lists.newArrayList());
                result.put("ydata", Lists.newArrayList());
                result.put("zdata", Lists.newArrayList());
            }
        } catch (Exception e) {
            log.error("获取商品转化率曲线失败", e);
        } finally {
            lock.unlock();
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getUserSpu(Long userId) {
        return insightMapper.getUserSpu(userId);
    }

    @Override
    public String getUserBuyOrder(Long userId, Long spuId) {
        return insightMapper.getUserBuyOrder(userId, spuId);
    }

    /**
     * 件单价随购买次数变化
     *
     * @param type
     * @param id
     * @param periodStartDt
     * @return
     */
    @Override
    public Map<String, Object> unitPriceInPurchaseTimes(String type, Long id, Long periodStartDt) {
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> dataList = insightMapper.unitPriceInPurchaseTimes(type, id, periodStartDt);
        List<String> xdata = dataList.stream().map(x -> String.valueOf(x.get("purch_times"))).collect(Collectors.toList());
        List<String> ydata = dataList.stream().map(x -> String.valueOf(x.get("uprice") == null ? "0" : x.get("uprice"))).collect(Collectors.toList());
        result.put("xdata", xdata);
        result.put("ydata", ydata);
        return result;
    }

    /**
     * 连带率随购买次数变化
     *
     * @param type
     * @param id
     * @param periodStartDt
     * @return
     */
    @Override
    public Map<String, Object> joinRateInPurchaseTimes(String type, Long id, Long periodStartDt) {
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> dataList = insightMapper.joinRateInPurchaseTimes(type, id, periodStartDt);
        List<String> xdata = dataList.stream().map(x -> String.valueOf(x.get("purch_times"))).collect(Collectors.toList());
        List<String> ydata = dataList.stream().map(x -> String.valueOf(x.get("joint") == null ? "0" : x.get("joint"))).collect(Collectors.toList());
        result.put("xdata", xdata);
        result.put("ydata", ydata);
        return result;
    }

    /**
     * 品类种数随购买次数变化
     *
     * @param type
     * @param id
     * @param periodStartDt
     * @return
     */
    @Override
    public Map<String, Object> categoryInPurchaseTimes(String type, Long id, Long periodStartDt) {
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> dataList = Lists.newArrayList();
        if (type.equalsIgnoreCase("spu")) {
            dataList = insightMapper.spuCategoryInPurchaseTimes(type, id, periodStartDt);
        } else if (type.equalsIgnoreCase("product")) {
            dataList = insightMapper.productCategoryInPurchaseTimes(type, id, periodStartDt);
        }

        List<String> xdata = dataList.stream().map(x -> String.valueOf(x.get("purch_times"))).collect(Collectors.toList());
        List<String> ydata = dataList.stream().map(x -> String.valueOf(x.get("avg_cate_num") == null ? "0" : x.get("avg_cate_num"))).collect(Collectors.toList());
        result.put("xdata", xdata);
        result.put("ydata", ydata);
        return result;
    }

    /**
     * 时间间隔随购买次数变化
     *
     * @param type
     * @param id
     * @param periodStartDt
     * @return
     */
    @Override
    public Map<String, Object> periodInPurchaseTimes(String type, Long id, Long periodStartDt) {
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> dataList = insightMapper.periodInPurchaseTimes(type, id, periodStartDt);
        List<String> xdata = dataList.stream().map(x -> String.valueOf(x.get("purch_times"))).collect(Collectors.toList());
        List<String> ydata = dataList.stream().map(x -> String.valueOf(x.get("avg_pur_gap") == null ? "0" : x.get("avg_pur_gap"))).collect(Collectors.toList());
        result.put("xdata", xdata);
        result.put("ydata", ydata);
        return result;
    }


    @Override
    public Map<String, Object> getUserSpuRelation(Long userId, Long spuId, Long buyOrder) {
        Map<String, Object> result = Maps.newHashMap();
        List<String> xdata = Lists.newArrayList();
        List<String> ebpProductIdList = Lists.newArrayList();
        // 实际数据
        List<Integer> ydataActual = Lists.newArrayList();
        // 辅助数据
        List<Integer> ydataReduce = Lists.newArrayList();
        List<Map<String, Object>> mapList = insightMapper.getSpuRelation(spuId, buyOrder);
        if (!mapList.isEmpty()) {
            String spuWid = (mapList.stream().findFirst().get().get("spu_wid")).toString();
            String spuName = (mapList.stream().findFirst().get().get("spu_name")).toString();
            Integer spuUserCnt = ((BigDecimal) mapList.stream().findFirst().get().get("spu_cnt")).intValue();
            xdata.add(spuName);
            ebpProductIdList.add(spuWid);
            ydataActual.add(spuUserCnt);
            mapList.stream().forEach(x -> {
                xdata.add(x.get("ebp_product_name").toString());
                ebpProductIdList.add(x.get("ebp_product_id").toString());
                ydataActual.add(Integer.parseInt(x.get("product_cnt").toString()));
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

            // 根据用户Id获取ebpProductId
            List<Map<String, String>> ebpProductMapList = insightMapper.getEbpProductIdByUserId(userId, spuId, buyOrder);
            Map<String, String> ebpProductMap = ebpProductMapList.get(0);
            Long ebpProductId = Long.parseLong(ebpProductMap.get("ebp_product_id"));
            Map<String, Object> convertMap = getProductConvertRate(ebpProductId, spuId, buyOrder);
            result.put("xdata2", convertMap.get("xdata"));
            result.put("ydata2", convertMap.get("ydata"));
            result.put("nextProductId", convertMap.get("nextProductId"));
            result.put("ebpProductMap", ebpProductMap);
        }

        result.put("xdata1", xdata);
        result.put("ydataActual", ydataActual);
        result.put("ydataReduce", ydataReduce);
        // 获取productId 和 nextProductId
        result.put("productId", ebpProductIdList);
        return result;
    }

    /**
     * 获取每日运营的日期距离最后一次购买的间隔
     *
     * @param spuId
     * @param spuId
     * @param userId
     * @return
     */
    @Override
    public long getUserBuyDual(Long spuId, Long userId, String taskDt) {
        String dateFormat = "yyyyMMdd";
        String lastBuyDt = insightMapper.getLastBuyDt(spuId, userId);
        LocalDate lastDt = LocalDate.parse(lastBuyDt, DateTimeFormatter.ofPattern(dateFormat));
        LocalDate currentDt = LocalDate.parse(taskDt, DateTimeFormatter.ofPattern(dateFormat));
        long until = lastDt.until(currentDt, ChronoUnit.DAYS);
        return until;
    }

    /**
     * 获取用户成长节点
     *
     * @return
     */
    @Override
    public List<Map<String, String>> getUserGrowthPathPoint(Long userId, Long spuId) {
        String dateFormat = "yyyyMMdd";
        List<Map<String, String>> userGrowthPathPointWithSpu = insightMapper.getUserGrowthPathPointWithSpu(userId, spuId);
        String lastBuyDt = insightMapper.getLastBuyDt(spuId, userId);
        LocalDate lastDt = LocalDate.parse(lastBuyDt, DateTimeFormatter.ofPattern(dateFormat));
        for (Map<String, String> x : userGrowthPathPointWithSpu) {
            x.put("last_buy_dt", lastBuyDt);
            LocalDate active_dual = lastDt.plusDays(Long.parseLong(String.valueOf(x.get("active_dual"))));
            x.put("growth_dt", active_dual.format(DateTimeFormatter.ofPattern(dateFormat)));
        }
        return userGrowthPathPointWithSpu;
    }

    @Override
    public Map<String, Object> getUserValueWithSpu(Long userId, Long spuId) {
        Map<String, Object> result = Maps.newHashMap();
        Map<String, String> data = insightMapper.getUserValueWithSpu(spuId, userId);
        if (null != data && !data.isEmpty()) {
            result.put("current", new LinkedList<>(data.values()));
        } else {
            result.put("current", new LinkedList<>());
        }
        return result;
    }

    @Override
    public Map<String, Object> getUserConvert(Long userId) {
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> data1 = insightMapper.getConvertDate(userId);
        List<Map<String, Object>> data2 = insightMapper.getPushDate(userId);
        List<Map<String, Object>> data3 = insightMapper.getPushAndConvertDate(userId);
        List<LinkedList<Object>> data1List = data1.stream().map(x -> new LinkedList<>(x.values())).collect(Collectors.toList());
        List<LinkedList<Object>> data2List = data2.stream().map(x -> new LinkedList<>(x.values())).collect(Collectors.toList());
        List<LinkedList<Object>> data3List = data3.stream().map(x -> new LinkedList<>(x.values())).collect(Collectors.toList());
        result.put("data1", data1List);
        result.put("data2", data2List);
        result.put("data3", data3List);
        return result;
    }

    @Override
    public Map<String, String> getUserGrowthData(String userId, String spuId) {
        Map<String, String> data = insightMapper.getGrowthData(userId, spuId);
        List<Dict> growthTypeList = dictMapper.getDataListByTypeCode("GROWTH_TYPE");
        List<Dict> growthSeriesTypeList = dictMapper.getDataListByTypeCode("GROWTH_SERIES_TYPE");
        String growthType = data.get("growth_type");
        String growthSeriesType = data.get("growth_series_type");
        if (StringUtils.isNotEmpty(growthSeriesType)) {
            List<String> growthSeriesTypeNameList = Arrays.asList(growthSeriesType.split(",")).stream().map(x -> growthSeriesTypeList.stream().filter(y -> y.getCode().equalsIgnoreCase(x)).findFirst().get().getValue()).collect(Collectors.toList());
            data.put("growth_series_type", String.join(",", growthSeriesTypeNameList));
        } else {
            data.put("growth_series_type", "");
        }
        growthTypeList.stream().filter(x -> x.getCode().equalsIgnoreCase(growthType)).findFirst().ifPresent(x -> data.put("growth_type", x.getValue()));

        return data;
    }

    @Override
    public int getGrowthTableDataCount(String startDt, String endDt, String userId) {
        return insightMapper.getGrowthTableDataCount(startDt, endDt, userId);
    }

    @Override
    public List<InsightUserEffect> getGrowthTableDataList(String startDt, String endDt, Integer limit, Integer offset, String userId) {
        return insightMapper.getGrowthTableDataList(startDt, endDt, limit, offset, userId);
    }

    @Override
    public Map<String, Object> allGrowthData(String startDt, String endDt) {
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> growthP = insightMapper.allGrowthP(startDt, endDt);
        List<Map<String, Object>> growthV = insightMapper.allGrowthV(startDt, endDt);
        if(growthP.size() == 0) {
            List<String> datePeriod = DateUtil.getPeriodDate("M", startDt, endDt);
            growthP = datePeriod.stream().map(x -> {
                Map<String, Object> tmp = Maps.newHashMap();
                tmp.put("date_", x);
                tmp.put("value_", 0);
                return tmp;
            }).collect(Collectors.toList());
        }
        if(growthV.size() == 0) {
            List<String> datePeriod = DateUtil.getPeriodDate("M", startDt, endDt);
            growthV = datePeriod.stream().map(x -> {
                Map<String, Object> tmp = Maps.newHashMap();
                tmp.put("date_", x);
                tmp.put("value_", 0);
                return tmp;
            }).collect(Collectors.toList());
        }
        result.put("growthP", growthP);
        result.put("growthV", growthV);
        return result;
    }

    @Override
    public Map<String, Object> singleGrowthData(String userId) {
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> growthP = insightMapper.singleGrowthP(userId);
        List<Map<String, Object>> growthV = insightMapper.singleGrowthV(userId);
        result.put("growthP", growthP);
        result.put("growthV", growthV);
        return result;
    }
}