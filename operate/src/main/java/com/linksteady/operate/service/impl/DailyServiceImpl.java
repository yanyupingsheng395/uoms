package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.operate.dao.DailyDetailMapper;
import com.linksteady.operate.dao.DailyMapper;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.service.ConfigService;
import com.linksteady.operate.service.DailyService;
import com.linksteady.operate.vo.DailyPersonalVo;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * 效果统计最大时间长度
     */
    private final int MAX_TASK_DAY = 10;

    @Override
    public List<DailyHead> getPageList(int start, int end, String touchDt) {
        return dailyMapper.getPageList(start, end, touchDt);
    }

    @Override
    public int getTotalCount(String touchDt) {
        return dailyMapper.getTotalCount(touchDt);
    }

    @Override
    public DailyHead getDailyHeadById(String headId) {
        return dailyMapper.getDailyHeadById(headId);
    }

    @Override
    public DailyHead getEffectById(String id) {
        return dailyMapper.getEffectById(id);
    }

    /**
     * 获取推送的数据 转化人数，转化率
     *
     * @param headId
     * @return
     */
    @Override
    public Map<String, Object> getPushData(String headId) {
        Map<String, Object> result = Maps.newHashMap();
        String dateFormat = "yyyy-MM-dd";
        List<String> xdatas = Lists.newLinkedList();

        // 提交任务日期
        String taskDt = dailyMapper.getDailyHeadById(headId).getTouchDtStr();
        LocalDate taskDtDate = LocalDate.parse(taskDt, DateTimeFormatter.ofPattern(dateFormat));
        // 任务期最后时间
        LocalDate maxDate = taskDtDate.plusDays(MAX_TASK_DAY + 1);
        // 判断当前时间与最后时间
        boolean after = LocalDate.now().isAfter(taskDtDate.plusDays(MAX_TASK_DAY + 1));
        String endDate;
        if (after) {
            endDate = maxDate.format(DateTimeFormatter.ofPattern(dateFormat));
        } else {
            endDate = LocalDate.now().format(DateTimeFormatter.ofPattern(dateFormat));
        }
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(dateFormat));
        while (taskDtDate.isBefore(end.plusDays(1))) {
            xdatas.add(taskDtDate.format(DateTimeFormatter.ofPattern(dateFormat)));
            taskDtDate = taskDtDate.plusDays(1);
        }

        List<DailyStatis> dataList = dailyMapper.getDailyStatisList(headId);
        Map<String, Long> convertNumMap = dataList.stream().collect(
                Collectors.toMap(
                        dailyStatis -> Optional.ofNullable(dailyStatis).map(DailyStatis::getConversionDateStr).orElse("0"),
                        dailyStatis -> Optional.ofNullable(dailyStatis).map(DailyStatis::getConvertNum).orElse(0L),
                        (k1, k2) -> k2
                )
        );
        Map<String, Double> convertRateMap = dataList.stream().collect(
                Collectors.toMap(
                        dailyStatis -> Optional.ofNullable(dailyStatis).map(DailyStatis::getConversionDateStr).orElse("0"),
                        dailyStatis -> Optional.ofNullable(dailyStatis).map(DailyStatis::getConvertRate).orElse(0D),
                        (k1, k2) -> k2
                )
        );
        Map<String, Long> convertSpuNumMap = dataList.stream().collect(

                Collectors.toMap(
                        dailyStatis -> Optional.ofNullable(dailyStatis).map(DailyStatis::getConversionDateStr).orElse("0"),
                        dailyStatis -> Optional.ofNullable(dailyStatis).map(DailyStatis::getConvertSpuNum).orElse(0L),
                        (k1, k2) -> k2
                )
        );
        Map<String, Double> convertSpuRateMap = dataList.stream().collect(
                Collectors.toMap(
                        dailyStatis -> Optional.ofNullable(dailyStatis).map(DailyStatis::getConversionDateStr).orElse("0"),
                        dailyStatis -> Optional.ofNullable(dailyStatis).map(DailyStatis::getConvertSpuRate).orElse(0D),
                        (k1, k2) -> k2
                )
        );
        xdatas.forEach(x -> {
            convertNumMap.putIfAbsent(x, 0L);
            convertRateMap.putIfAbsent(x, 0D);
            convertSpuNumMap.putIfAbsent(x, 0L);
            convertSpuRateMap.putIfAbsent(x, 0D);
        });

        result.put("xdata", xdatas);
        result.put("ydata1", new ArrayList<>(convertNumMap.values()));
        result.put("ydata2", new ArrayList<>(convertRateMap.values()));
        result.put("ydata3", new ArrayList<>(convertSpuNumMap.values()));
        result.put("ydata4", new ArrayList<>(convertSpuRateMap.values()));
        return result;
    }

    @Override
    public List<DailyGroupTemplate> getUserGroupList() {
        String active = configService.getValueByName("op.daily.pathactive.list");
        List<String> activeList = null;
        if (StringUtils.isNotEmpty(active)) {
            activeList = Arrays.asList(active.split(","));
        }
        return dailyMapper.getUserGroupList(activeList);
    }

    @Override
    public void setSmsCode(String groupId, String smsCode) {
        if (StringUtils.isNotEmpty(groupId)) {
            List<String> groupIds = Arrays.asList(groupId.split(","));
            dailyMapper.setSmsCode(groupIds, smsCode);
        }
    }

    @Override
    public List<DailyPersonal> getDailyPersonalEffect(DailyPersonalVo dailyPersonalVo, int start, int end, String headId) {
        return dailyMapper.getDailyPersonalEffect(dailyPersonalVo, start, end, headId);
    }

    @Override
    public int getDailyPersonalEffectCount(DailyPersonalVo dailyPersonalVo, String headId) {
        return dailyMapper.getDailyPersonalEffectCount(dailyPersonalVo, headId);
    }

    @Override
    public void updateHeaderOpChangeDate(String headId, Long opChangeDate) {
        dailyMapper.updateHeaderOpChangeDate(headId, opChangeDate);
    }

    @Override
    public boolean getTransContentLock(String headId) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //如果key不存在，则将key的值设置为value，同时返回true. 如果key不存在，则什么也不做，返回false.
        boolean flag = valueOperations.setIfAbsent("daily_trans_lock", headId,60, TimeUnit.SECONDS);
        return flag;
    }

    @Override
    public void delTransLock() {
        redisTemplate.delete("daily_trans_lock");
    }

    @Override
    public int validateOpChangeTime(String headId, Long opChangeDate) {
        return dailyMapper.validateOpChangeTime(headId, opChangeDate);
    }

    @Override
    public List<DailyUserStats> getUserStats(String headerId) {
        return dailyMapper.getUserStats(headerId);
    }

    @Override
    public List<DailyUserStats> getUserStatsBySpu(String headerId, String userValue, String pathActive, String lifecycle) {
        return dailyMapper.getUserStatsBySpu(headerId, userValue, pathActive, lifecycle);
    }

    @Override
    public List<DailyUserStats> getUserStatsByProd(String headerId, String userValue, String pathActive, String lifecycle, String spuName) {
        return dailyMapper.getUserStatsByProd(headerId, userValue, pathActive, lifecycle, spuName);
    }

    @Override
    public Map<String, Object> getSelectedUserGroup(String groupId) {
        return dailyMapper.getSelectedUserGroup(groupId);
    }

    @Override
    public int getSmsIsCoupon(String smsCode, String isCoupon) {
        return dailyMapper.getSmsIsCoupon(smsCode, isCoupon);
    }

    @Override
    public int getValidDailyHead() {
        return dailyMapper.getValidDailyHead();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSmsCodeNull(String smsCode) {
        dailyMapper.updateSmsCodeNull(smsCode);
    }

    @Override
    public Map<String, Object> getUserStatsData(String headId) {
        Map<String, Object> result = Maps.newHashMap();
        Map<String, String> pathActiveMap =configService.selectDictByTypeCode("PATH_ACTIVE");
        Map<String, String> userValueMap =configService.selectDictByTypeCode("USER_VALUE");
        Map<String, String> lifeCycleMap =configService.selectDictByTypeCode("LIFECYCLE");
        //获取人群分布
        List<DailyUserStats> dailyUserStats = getUserStats(headId);
        //设置标签的显示值
        dailyUserStats.stream().forEach(p -> {
            p.setUserValueLabel(userValueMap.get(p.getUserValue()));
            p.setGetPathActivityLabel(pathActiveMap.get(p.getPathActivity()));
            p.setLifecycleLabel(lifeCycleMap.get(p.getLifecycle()));
        });

        JSONArray fparray = new JSONArray();
        JSONArray rparray = new JSONArray();
        JSONArray fpTemp = null;
        for (DailyUserStats d1 : dailyUserStats) {
            fpTemp = new JSONArray();
            /**
             * x轴的数据 人数
             */
            fpTemp.add(d1.getUcnt());
            /**
             * y轴 用户活跃度
             */
            fpTemp.add(d1.getGetPathActivityLabel());

            //价值的大小
            int formatSize = 0;
            if ("ULC_01".equals(d1.getUserValue())) {
                formatSize = 80;
            } else if ("ULC_02".equals(d1.getUserValue())) {
                formatSize = 60;
            } else if ("ULC_03".equals(d1.getUserValue())) {
                formatSize = 40;
            } else if ("ULC_04".equals(d1.getUserValue())) {
                formatSize = 20;
            }
            fpTemp.add(formatSize);
            fpTemp.add(d1.getLifecycleLabel());
            fpTemp.add("人数:" + d1.getUcnt() + ",占比:" + d1.getPct() + "%");
            fpTemp.add(d1.getUserValue());
            fpTemp.add(d1.getPathActivity());
            fpTemp.add(d1.getLifecycle());

            if ("1".equals(d1.getLifecycle())) {
                fparray.add(fpTemp);
            } else {
                rparray.add(fpTemp);
            }
        }
        //首购用户数据
        result.put("fpUser", fparray);
        //复购用户数据
        result.put("rpUser", rparray);

        List<String> yLabelList = Lists.newArrayList();
        Set<String> activeSet = dailyUserStats.stream().map(DailyUserStats::getPathActivity).collect(Collectors.toSet());
        for (Map.Entry<String, String> entry : pathActiveMap.entrySet()) {
            if (activeSet.contains(entry.getKey())) {
                yLabelList.add(entry.getValue());
            }
        }
        result.put("ylabel", yLabelList);

        //获取排名第一的群组 获取其类目信息
        DailyUserStats dailyUserStats1 = dailyUserStats.get(0);
        if (null != dailyUserStats1) {
            //获取用户在SPU上top 10
            List<DailyUserStats> spuList = getUserStatsBySpu(headId, dailyUserStats1.getUserValue(), dailyUserStats1.getPathActivity(), dailyUserStats1.getLifecycle());
            result.put("spuList", spuList);
            result.put("groupName", dailyUserStats1.getUserValueLabel() + "-" + dailyUserStats1.getGetPathActivityLabel() + "-" + dailyUserStats1.getLifecycleLabel() + "群组");
            result.put("userValue", dailyUserStats1.getUserValue());
            result.put("pathActive", dailyUserStats1.getPathActivity());
            result.put("lifecycle", dailyUserStats1.getLifecycle());

            DailyUserStats dailyUserStats2 = spuList.get(0);
            //获取top10 推荐商品
            if (null != dailyUserStats2) {
                List<DailyUserStats> prodList = getUserStatsByProd(headId, dailyUserStats1.getUserValue(), dailyUserStats1.getPathActivity(), dailyUserStats1.getLifecycle(), dailyUserStats2.getSpuName());
                result.put("prodList", prodList);
                result.put("prodGroupName", dailyUserStats1.getUserValueLabel() + "-" + dailyUserStats1.getGetPathActivityLabel() + "-" + dailyUserStats1.getLifecycleLabel() + "群组," + dailyUserStats2.getSpuName() + "类目");
                result.put("spuName", dailyUserStats2.getSpuName());
            } else {
                result.put("prodList", Lists.newArrayList());
                result.put("prodGroupName", "");
                result.put("spuName", "");
            }

        } else {
            result.put("spuList", Lists.newArrayList());
            result.put("groupName", "");
            result.put("userValue", "");
            result.put("pathActive", "");
            result.put("lifecycle", "");
        }

        //获取当前运营头表
        DailyHead dailyHead = getDailyHeadById(headId);
        result.put("touchDt", new SimpleDateFormat("yyyy年MM月dd日").format(dailyHead.getTouchDt()));
        result.put("userNum", dailyHead.getTotalNum());
        return result;
    }

    /**
     * 对每日运营进行推送
     * @param headId
     * @param pushMethod
     * @param pushPeriod
     * @param timestamp
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pushContent(String headId, String pushMethod, String pushPeriod, Long timestamp,Long effectDays) {
        //更新时间戳
        long timestampNew = System.currentTimeMillis();

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
        // 默认是AI：plan_push_period = order_period 此时，pushOrderPeriod = ""

        //更新时间戳、推送方式、推送时段到头表
        dailyMapper.updateHeaderPushInfo(headId, timestampNew,pushMethod,pushOrderPeriod,effectDays);

        //更新行上的push_order_period
        dailyDetailMapper.updatePushOrderPeriod(headId);

        //复制写入待推送列表
        dailyDetailMapper.copyToPushList(headId);

        //更新状态为已执行
        dailyMapper.updateStatus(headId, "done");

    }
}
