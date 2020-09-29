package com.linksteady.operate.service.impl;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.dao.DailyDetailMapper;
import com.linksteady.operate.dao.DailyMapper;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.exception.OptimisticLockException;
import com.linksteady.operate.service.DailyService;
import com.linksteady.operate.vo.DailyPersonalVO;
import com.linksteady.operate.vo.DailyUserStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 日运营头表
 *
 * @author hxcao
 * @date 2019-07-31
 */
@Service
public class DailyServiceImpl implements DailyService {

    @Autowired
    private DailyMapper dailyMapper;

    @Autowired
    private DailyDetailMapper dailyDetailMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    ConfigService configService;

    @Override
    public List<DailyHead> getPageList(int limit, int offset, String touchDt) {
        return dailyMapper.getPageList(limit, offset, touchDt);
    }

    @Override
    public int getTotalCount(String touchDt) {
        return dailyMapper.getTotalCount(touchDt);
    }

    @Override
    public DailyHead getDailyHeadById(Long headId) {
        return dailyMapper.getDailyHeadById(headId);
    }

    @Override
    public DailyHead getEffectById(Long id) {
        return dailyMapper.getEffectById(id);
    }

    /**
     * 获取推送的数据 转化人数，转化率
     *
     * @param headId
     * @return
     */
    @Override
    public Map<String, Object> getPushData(Long headId) {
        Map<String, Object> result = Maps.newHashMap();
        String dateFormat = "yyyyMMdd";
        List<LocalDate> xdatas = Lists.newLinkedList();

        DailyHead dailyHead = dailyMapper.getDailyHeadById(headId);
        // 提交任务日期
        String taskDt = dailyHead.getTouchDtStr();

        //获取任务观察的天数
        int effectDays = dailyHead.getEffectDays().intValue();

        //任务提交的日期
        LocalDate taskDtDate = LocalDate.parse(taskDt, DateTimeFormatter.ofPattern(dateFormat));

        // 任务期最后时间
        LocalDate maxDate = taskDtDate.plusDays(effectDays + 1);

        //时间轴的数据
        while (taskDtDate.isBefore(maxDate)) {
            xdatas.add(taskDtDate);
            taskDtDate = taskDtDate.plusDays(1);
        }

        List<DailyStatis> dataList = dailyMapper.getDailyStatisList(headId);
        Map<String, DailyStatis> dailyStatisMap = dataList.stream().collect(Collectors.toMap(DailyStatis::getConversionDateStr, a -> a));

        //转化人数
        List<Long> convertNumList = Lists.newArrayList();
        List<Double> convertRateList = Lists.newArrayList();
        List<Long> convertSpuNumList = Lists.newArrayList();
        List<Double> convertSpuRateList = Lists.newArrayList();

        xdatas.forEach(x -> {
            //判断当前是否有数据
            DailyStatis dailyStatis = dailyStatisMap.get(x.format(DateTimeFormatter.ofPattern(dateFormat)));

            //找不到转化数据
            if (null == dailyStatis) {
                if (x.isAfter(LocalDate.now()) || x.isEqual(LocalDate.now())) {
                    //填充空值
                    convertNumList.add(null);
                    convertRateList.add(null);
                    convertSpuNumList.add(null);
                    convertSpuRateList.add(null);
                } else {
                    //填充0
                    convertNumList.add(0L);
                    convertRateList.add(0D);
                    convertSpuNumList.add(0L);
                    convertSpuRateList.add(0D);
                }
            } else {
                convertNumList.add(dailyStatis.getConvertNum());
                convertRateList.add(dailyStatis.getConvertRate());
                convertSpuNumList.add(dailyStatis.getConvertSpuNum());
                convertSpuRateList.add(dailyStatis.getConvertSpuRate());
            }
        });

        result.put("xdata", xdatas.stream().map(x -> x.format(DateTimeFormatter.ofPattern(dateFormat))).collect(Collectors.toList()));
        result.put("ydata1", convertNumList);
        result.put("ydata2", convertRateList);
        result.put("ydata3", convertSpuNumList);
        result.put("ydata4", convertSpuRateList);
        return result;
    }


    @Override
    public List<DailyPersonal> getDailyPersonalEffect(DailyPersonalVO dailyPersonalVo, int limit, int offset, String headId) {
        return dailyMapper.getDailyPersonalEffect(dailyPersonalVo, limit, offset, headId);
    }

    @Override
    public int getDailyPersonalEffectCount(DailyPersonalVO dailyPersonalVo, String headId) {
        return dailyMapper.getDailyPersonalEffectCount(dailyPersonalVo, headId);
    }

    @Override
    public boolean getTransContentLock(String headId) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //如果key不存在，则将key的值设置为value，同时返回true. 如果key不存在，则什么也不做，返回false.
        boolean flag = valueOperations.setIfAbsent("daily_trans_lock", headId, 60, TimeUnit.SECONDS);
        return flag;
    }

    @Override
    public void delTransLock() {
        redisTemplate.delete("daily_trans_lock");
    }

    /**
     * 获取推送预览的数据
     *
     * @param headId
     * @return
     */
    @Override
    public Map<String, Object> getUserStatsData(Long headId) {
        Map<String, Object> result = Maps.newHashMap();

        //获取按商品、spu的统计数据
        List<DailyUserStatsVO> statsBySpu = dailyMapper.getTargetInfoBySpu(headId);

        List<String> spuList = statsBySpu.stream().map(DailyUserStatsVO::getSpuName).collect(Collectors.toList());
        List<Integer> spuCountList = statsBySpu.stream().map(DailyUserStatsVO::getUcnt).collect(Collectors.toList());

        //获取最后一个SPU下的商品信息
        List<DailyUserStatsVO> statsByProd = dailyMapper.getTargetInfoByProd(headId, spuList.get(spuList.size() - 1));

        List<String> prodList = statsByProd.stream().map(DailyUserStatsVO::getProdName).collect(Collectors.toList());
        List<Integer> prodCountList = statsByProd.stream().map(DailyUserStatsVO::getUcnt).collect(Collectors.toList());

        //按价值的用户分布
        List<DailyUserStatsVO> statsByUserValue = dailyMapper.getTargetInfoByUserValue(headId);
        List<String> userValueList = statsByUserValue.stream().map(DailyUserStatsVO::getUserValue).collect(Collectors.toList());
        List<String> userValueLabelList = statsByUserValue.stream().map(DailyUserStatsVO::getUserValueLabel).collect(Collectors.toList());
        List<Integer> userValueCountList = statsByUserValue.stream().map(DailyUserStatsVO::getUcnt).collect(Collectors.toList());

        Map<String, Object> matrixResult = getMatrixData(headId, statsByUserValue.get(statsByUserValue.size() - 1).getUserValue());

        //按个性化补贴的分布
        List<DailyUserStatsVO> statsByCoupon = dailyMapper.getTargetInfoByCoupon(headId);

        //按用户成长目标的分布
        List<DailyUserStatsVO> statsByGrowthType = dailyMapper.getTargetInfoByGrowthType(headId);
        List<String> growthTypeLabelList = statsByGrowthType.stream().map(DailyUserStatsVO::getGrowthType).collect(Collectors.toList());
        List<Integer> growthTypeCountList = statsByGrowthType.stream().map(DailyUserStatsVO::getUcnt).collect(Collectors.toList());

        //按用户成长目标【序列】的分布
        List<DailyUserStatsVO> statsByGrowthTypeSeries = dailyMapper.getTargetInfoByGrowthSeriesType(headId);
        List<String> growthSeriesTypeLabelList = statsByGrowthTypeSeries.stream().map(DailyUserStatsVO::getGrowthSeriesType).collect(Collectors.toList());
        List<Integer> growthSeriesTypeCountList = statsByGrowthTypeSeries.stream().map(DailyUserStatsVO::getUcnt).collect(Collectors.toList());

        DailyHead dailyHead = dailyMapper.getDailyHeadById(headId);

        result.put("spuList", spuList);
        result.put("spuCountList", spuCountList);

        result.put("prodList", prodList);
        result.put("prodCountList", prodCountList);

        result.put("userValueList", userValueList);
        result.put("userValueLabelList", userValueLabelList);
        result.put("userValueCountList", userValueCountList);

        result.put("matrixResult", matrixResult);


        result.put("growthTypeLabelList", growthTypeLabelList);
        result.put("growthTypeCountList", growthTypeCountList);

        result.put("growthSeriesTypeLabelList", growthSeriesTypeLabelList);
        result.put("growthSeriesTypeCountList", growthSeriesTypeCountList);

        result.put("statsByCoupon", statsByCoupon);

        result.put("touchDt", new SimpleDateFormat("yyyy年MM月dd日").format(dailyHead.getTouchDt()));
        result.put("userNum", dailyHead.getTotalNum());

        return result;
    }

    /**
     * 获取给定SPU下的商品分布数据
     *
     * @param headId
     * @param spuName
     * @return
     */
    @Override
    public Map<String, Object> getProdCountBySpu(Long headId, String spuName) {
        Map<String, Object> result = Maps.newHashMap();
        List<DailyUserStatsVO> statsByProd = dailyMapper.getTargetInfoByProd(headId, spuName);

        List<String> prodList = statsByProd.stream().map(DailyUserStatsVO::getProdName).collect(Collectors.toList());
        List<Integer> prodCountList = statsByProd.stream().map(DailyUserStatsVO::getUcnt).collect(Collectors.toList());

        result.put("prodList", prodList);
        result.put("prodCountList", prodCountList);

        return result;
    }

    /**
     * 根据给定 用户价值 获取 活跃度和生命周期的交叉表格数据
     *
     * @param headId
     * @param userValue
     * @return
     */
    @Override
    public Map<String, Object> getMatrixData(Long headId, String userValue) {
        Map<String, String> pathActiveMap = configService.selectDictByTypeCode("PATH_ACTIVE");
        Map<String, String> lifeCycleMap = configService.selectDictByTypeCode("LIFECYCLE");

        //给定价值下 在生命周期和活跃度上的分布表格
        List<DailyUserStatsVO> statsMatrix = dailyMapper.getTargetInfoMatrix(headId, userValue);
        //转化成table 方便同时通过活跃度和生命周期进行查找
        Table<String, String, Integer> tables = HashBasedTable.create();
        statsMatrix.forEach(i -> {
            tables.put(i.getPathActivity(), i.getLifecycle(), i.getUcnt());
        });

        Map<String, Object> matrixResult = Maps.newHashMap();
        matrixResult.put("columnTitle", lifeCycleMap.values().toArray());
        List<List<String>> rows = Lists.newArrayList();

        List<String> row = null;
        //遍历活跃度
        for (Map.Entry<String, String> pathActive : pathActiveMap.entrySet()) {
            row = Lists.newArrayList();
            row.add(pathActive.getValue());

            //对每一个活跃度遍历 生命周期
            for (Map.Entry<String, String> lifeCycle : lifeCycleMap.entrySet()) {
                //判断是否存在对应的值
                if (tables.contains(pathActive.getKey(), lifeCycle.getKey())) {
                    row.add(String.valueOf(tables.get(pathActive.getKey(), lifeCycle.getKey())));
                } else {
                    row.add("0");
                }

            }
            rows.add(row);
        }
        matrixResult.put("rows", rows);

        return matrixResult;
    }


    /**
     * 对每日运营进行推送
     *
     * @param dailyHead
     * @param pushMethod
     * @param pushPeriod
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pushContent(DailyHead dailyHead, String pushMethod, String pushPeriod, Long effectDays) throws Exception {
        //更新状态为已执行
        int count = dailyMapper.updateStatus(dailyHead.getHeadId(), "done", dailyHead.getVersion());

        if (count == 0) {
            throw new OptimisticLockException("记录已被其他用户修改，请返回刷新后重试");
        }

        // 推送方式 IMME立即推送 AI智能推送 FIXED固定时间推送
        String pushOrderPeriod = "";
        // 立即推送：当前时间往后顺延10分钟
        if ("IMME".equalsIgnoreCase(pushMethod)) {
            pushOrderPeriod = String.valueOf(LocalTime.now().plusMinutes(10).getHour());
        }

        // 固定时间推送：参数获取
        if ("FIXED".equalsIgnoreCase(pushMethod)) {
            pushOrderPeriod = String.valueOf(LocalTime.parse(pushPeriod, DateTimeFormatter.ofPattern("HH:mm")).getHour());
        }

        //更新时间戳、推送方式、推送时段到头表
        dailyMapper.updateHeaderPushInfo(dailyHead.getHeadId(), pushMethod, pushOrderPeriod, effectDays);

        //更新行上的push_order_period
        dailyDetailMapper.updatePushOrderPeriod(dailyHead.getHeadId());

        //更新试验组的状态为TS
        dailyDetailMapper.updateExperimentStatus(dailyHead.getHeadId());
        //复制写入待推送列表(对照组及不参加组)
        dailyDetailMapper.copyToPushList(dailyHead.getHeadId());
    }

    @Override
    public void expireDailyHead() {
        dailyMapper.expireDailyHead();
    }

    @Override
    public String getLifeCycleByUserId(String userId, String headId) {
        return dailyMapper.getLifeCycleByUserId(userId, headId);
    }
}
