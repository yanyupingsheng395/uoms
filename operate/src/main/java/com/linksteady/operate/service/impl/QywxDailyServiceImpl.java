package com.linksteady.operate.service.impl;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.dao.QywxDailyDetailMapper;
import com.linksteady.operate.dao.QywxDailyMapper;
import com.linksteady.operate.domain.DailyHead;
import com.linksteady.operate.domain.DailyStatis;
import com.linksteady.operate.domain.QywxDailyDetail;
import com.linksteady.operate.domain.QywxDailyHeader;
import com.linksteady.operate.exception.OptimisticLockException;
import com.linksteady.operate.service.QywxDailyService;
import com.linksteady.operate.vo.QywxUserStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 日运营头表
 *
 * @author hxcao
 * @date 2019-07-31
 */
@Service
public class QywxDailyServiceImpl implements QywxDailyService {

    @Autowired
    private QywxDailyMapper qywxDailyMapper;

    @Autowired
    private QywxDailyDetailMapper qywxDailyDetailMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    ConfigService configService;

    @Override
    public List<QywxDailyHeader> getHeadList(int limit, int offset, String taskDate) {
        return qywxDailyMapper.getHeadList(limit, offset, taskDate);
    }

    @Override
    public int getTotalCount(String touchDt) {
        return qywxDailyMapper.getTotalCount(touchDt);
    }

    @Override
    public Map<String, Object> getTaskOverViewData(Long headId) {
        Map<String,Object> result=Maps.newHashMap();

        Map<String, String> pathActiveMap =configService.selectDictByTypeCode("PATH_ACTIVE");
        Map<String, String> lifeCycleMap =configService.selectDictByTypeCode("LIFECYCLE");

        //获取按商品、spu的统计数据
        List<QywxUserStats> statsBySpu=qywxDailyMapper.getTargetInfoBySpu(headId);

        List<String> spuList=statsBySpu.stream().map(QywxUserStats::getSpuName).collect(Collectors.toList());
        List<Integer> spuCountList=statsBySpu.stream().map(QywxUserStats::getUcnt).collect(Collectors.toList());

        //获取最后一个SPU下的商品信息
        List<QywxUserStats> statsByProd=qywxDailyMapper.getTargetInfoByProd(headId,spuList.get(spuList.size()-1));

        List<String> prodList=statsByProd.stream().map(QywxUserStats::getProdName).collect(Collectors.toList());
        List<Integer> prodCountList=statsByProd.stream().map(QywxUserStats::getUcnt).collect(Collectors.toList());

        //按价值的用户分布
        List<QywxUserStats> statsByUserValue=qywxDailyMapper.getTargetInfoByUserValue(headId);
        List<String> userValueLabelList=statsByUserValue.stream().map(QywxUserStats::getUserValueLabel).collect(Collectors.toList());
        List<Integer> userValueCountList=statsByUserValue.stream().map(QywxUserStats::getUcnt).collect(Collectors.toList());

        //给定价值下 在生命周期和活跃度上的分布表格
        List<QywxUserStats> statsMatrix=qywxDailyMapper.getTargetInfoMatrix(headId,statsByUserValue.get(0).getUserValue());
        //转化成table 方便同时通过活跃度和生命周期进行查找
        Table<String,String,Integer> tables= HashBasedTable.create();
        statsMatrix.forEach(i->{
            tables.put(i.getPathActivity(),i.getLifecycle(),i.getUcnt());
        });

        Map<String,Object> matrixResult=Maps.newHashMap();
        matrixResult.put("columnTitle",lifeCycleMap.values().toArray());
        List<List<String>> rows=Lists.newArrayList();

        List<String> row=null;
        //遍历活跃度
        for (Map.Entry<String, String> pathActive : pathActiveMap.entrySet()) {
            row=Lists.newArrayList();
            row.add(pathActive.getValue());

             //对每一个活跃度遍历 生命周期
            for (Map.Entry<String, String> lifeCycle : lifeCycleMap.entrySet()) {
                //判断是否存在对应的值
                if(tables.contains(pathActive.getKey(),lifeCycle.getKey()))
                {
                    row.add(String.valueOf(tables.get(pathActive.getKey(),lifeCycle.getKey())));
                }else
                {
                    row.add("0");
                }

            }
            rows.add(row);
        }
        matrixResult.put("rows",rows);

        //按成员的分布
        List<QywxUserStats> statsByUser=qywxDailyMapper.getTargetInfoByUser(headId);
        List<String> qywxUserList=statsByUser.stream().map(QywxUserStats::getQywxUser).collect(Collectors.toList());
        List<Integer> qywxCountList=statsByUser.stream().map(QywxUserStats::getUcnt).collect(Collectors.toList());

        //按个性化补贴的分布
        List<QywxUserStats> statsByCoupon=qywxDailyMapper.getTargetInfoByCoupon(headId);

        QywxDailyHeader qywxDailyHeader=qywxDailyMapper.getHeadInfo(headId);

        result.put("spuList",spuList);
        result.put("spuCountList",spuCountList);

        result.put("prodList",prodList);
        result.put("prodCountList",prodCountList);

        result.put("userValueLabelList",userValueLabelList);
        result.put("userValueCountList",userValueCountList);

        result.put("matrixResult",matrixResult);


        result.put("qywxUserList",qywxUserList);
        result.put("qywxCountList",qywxCountList);

        result.put("statsByCoupon",statsByCoupon);

        result.put("taskDate",new SimpleDateFormat("yyyy年MM月dd日").format(qywxDailyHeader.getTaskDate()));
        result.put("userNum",qywxDailyHeader.getTotalNum());
        return result;
    }

    @Override
    public boolean getTransContentLock(String headId) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //如果key不存在，则将key的值设置为value，同时返回true. 如果key不存在，则什么也不做，返回false.
        boolean flag = valueOperations.setIfAbsent("qywx_daily_trans_lock", headId,60, TimeUnit.SECONDS);
        return flag;
    }

    @Override
    public void delTransLock() {
        redisTemplate.delete("ywx_daily_trans_lock");
    }

    @Override
    public QywxDailyHeader getHeadInfo(Long headId) {
        return qywxDailyMapper.getHeadInfo(headId);
    }

    /**
     * 对每日运营进行推送
     * @param qywxDailyHeader
     * @param pushMethod
     * @param pushPeriod
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void push(QywxDailyHeader qywxDailyHeader, String pushMethod, String pushPeriod, Long effectDays) throws Exception{
        //更新状态为已执行
        int count=qywxDailyMapper.updateStatus(qywxDailyHeader.getHeadId(), "done",qywxDailyHeader.getVersion());

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
        qywxDailyMapper.updateHeaderPushInfo(qywxDailyHeader.getHeadId(),pushMethod,pushOrderPeriod,effectDays);

        //更新行上的push_order_period
        qywxDailyDetailMapper.updatePushOrderPeriod(qywxDailyHeader.getHeadId());

        //复制写入待推送列表
        qywxDailyDetailMapper.copyToPushList(qywxDailyHeader.getHeadId());

    }

    @Override
    public Map<String, Object> getPushEffectChange(Long headId) {
        Map<String, Object> result = Maps.newHashMap();
        String dateFormat = "yyyyMMdd";
        List<LocalDate> xdatas = Lists.newLinkedList();

        QywxDailyHeader qywxDailyHeader=qywxDailyMapper.getHeadInfo(headId);
        // 提交任务日期
        String taskDt =qywxDailyHeader.getTaskDateStr();

        //获取任务观察的天数
        int effectDays=qywxDailyHeader.getEffectDays().intValue();

        //任务提交的日期
        LocalDate taskDtDate = LocalDate.parse(taskDt, DateTimeFormatter.ofPattern(dateFormat));

        // 任务期最后时间
        LocalDate maxDate = taskDtDate.plusDays(effectDays+1);

        //时间轴的数据
        while (taskDtDate.isBefore(maxDate)) {
            xdatas.add(taskDtDate);
            taskDtDate = taskDtDate.plusDays(1);
        }

        List<DailyStatis> dataList = Lists.newArrayList();
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

}
