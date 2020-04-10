package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.dao.DailyDetailMapper;
import com.linksteady.operate.dao.DailyMapper;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.exception.OptimisticLockException;
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

    @Override
    public List<DailyHead> getPageList(int limit, int offset, String touchDt) {
        return dailyMapper.getPageList(limit, offset, touchDt);
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
        String dateFormat = "yyyyMMdd";
        List<LocalDate> xdatas = Lists.newLinkedList();

        DailyHead dailyHead=dailyMapper.getDailyHeadById(headId);
        // 提交任务日期
        String taskDt =dailyHead.getTouchDtStr();

        //获取任务观察的天数
        int effectDays=dailyHead.getEffectDays().intValue();

        //任务提交的日期
        LocalDate taskDtDate = LocalDate.parse(taskDt, DateTimeFormatter.ofPattern(dateFormat));

        // 任务期最后时间
        LocalDate maxDate = taskDtDate.plusDays(effectDays+1);

        //时间轴的数据
        while (taskDtDate.isBefore(maxDate)) {
            xdatas.add(taskDtDate);
            taskDtDate = taskDtDate.plusDays(1);
        }

        List<DailyStatis> dataList = dailyMapper.getDailyStatisList(headId);
        Map<String,DailyStatis> dailyStatisMap=dataList.stream().collect(Collectors.toMap(DailyStatis::getConversionDateStr,a->a));

        //转化人数
        List<Long> convertNumList=Lists.newArrayList();
        List<Double> convertRateList =Lists.newArrayList();
        List<Long> convertSpuNumList=Lists.newArrayList();
        List<Double> convertSpuRateList=Lists.newArrayList();

        xdatas.forEach(x->{
             //判断当前是否有数据
            DailyStatis dailyStatis=dailyStatisMap.get(x.format(DateTimeFormatter.ofPattern(dateFormat)));

            //找不到转化数据
            if(null==dailyStatis)
            {
                if(x.isAfter(LocalDate.now())||x.isEqual(LocalDate.now()))
                {
                    //填充空值
                    convertNumList.add(null);
                    convertRateList.add(null);
                    convertSpuNumList.add(null);
                    convertSpuRateList.add(null);
                }else
                {
                    //填充0
                    convertNumList.add(0L);
                    convertRateList.add(0D);
                    convertSpuNumList.add(0L);
                    convertSpuRateList.add(0D);
                }
            }else
            {
                convertNumList.add(dailyStatis.getConvertNum());
                convertRateList.add(dailyStatis.getConvertRate());
                convertSpuNumList.add(dailyStatis.getConvertSpuNum());
                convertSpuRateList.add(dailyStatis.getConvertSpuRate());
            }
        });

        result.put("xdata", xdatas.stream().map(x->x.format(DateTimeFormatter.ofPattern(dateFormat))).collect(Collectors.toList()));
        result.put("ydata1", convertNumList);
        result.put("ydata2", convertRateList);
        result.put("ydata3", convertSpuNumList);
        result.put("ydata4", convertSpuRateList);
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
    public List<DailyPersonal> getDailyPersonalEffect(DailyPersonalVo dailyPersonalVo, int limit, int offset, String headId) {
        return dailyMapper.getDailyPersonalEffect(dailyPersonalVo, limit, offset, headId);
    }

    @Override
    public int getDailyPersonalEffectCount(DailyPersonalVo dailyPersonalVo, String headId) {
        return dailyMapper.getDailyPersonalEffectCount(dailyPersonalVo, headId);
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
        DailyUserStats dailyUserStats1 = dailyUserStats.size() > 0 ? dailyUserStats.get(0): null;
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
     * @param dailyHead
     * @param pushMethod
     * @param pushPeriod
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pushContent(DailyHead dailyHead, String pushMethod, String pushPeriod,Long effectDays) throws Exception{

        //更新状态为已执行
        int count=dailyMapper.updateStatus(dailyHead.getHeadId(), "done",dailyHead.getVersion());

        if(count==0)
        {
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
        dailyMapper.updateHeaderPushInfo(dailyHead.getHeadId(),pushMethod,pushOrderPeriod,effectDays);

        //更新行上的push_order_period
        dailyDetailMapper.updatePushOrderPeriod(dailyHead.getHeadId());

        //复制写入待推送列表
        dailyDetailMapper.copyToPushList(dailyHead.getHeadId());

    }

    @Override
    public String getTouchDt(String headId) {
        return dailyMapper.getDailyHeadById(headId).getTouchDtStr();
    }

    @Override
    public void expireDailyHead() {
        dailyMapper.expireDailyHead();
    }
}
