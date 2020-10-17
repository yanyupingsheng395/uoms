package com.linksteady.qywx.service.impl;

import com.google.common.collect.Maps;
import com.linksteady.qywx.dao.UserTaskMapper;
import com.linksteady.qywx.domain.SpuInfo;
import com.linksteady.qywx.domain.UserBuyHistory;
import com.linksteady.qywx.service.UserTaskService;
import com.linksteady.qywx.vo.UserPurchSpuStatsVO;
import com.linksteady.qywx.vo.UserPurchStatsVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/7/2
 */
@Service
@Slf4j
public class UserTaskServiceImpl implements UserTaskService {

    @Autowired
    private UserTaskMapper userTaskMapper;

    @Override
    public Map<String, Object> getUserData(String userId, String productId) {
        Map<String, Object> result = Maps.newHashMap();

        // 用户今日所处的活跃状态 userTodayStatus
        List<Map<String, Object>> userTodayStatusList = userTaskMapper.getUserTodayStatus(userId, productId);
        String dateFormat = "yyyyMMdd";
        String lastBuyDt = userTaskMapper.getUserLastBuyDt(productId, userId);

        if(StringUtils.isNotEmpty(lastBuyDt)) {
            LocalDate lastDt = LocalDate.parse(lastBuyDt, DateTimeFormatter.ofPattern(dateFormat));
            boolean flag = false;
            for (Map<String, Object> x : userTodayStatusList) {
                x.put("last_buy_dt", lastBuyDt);
                LocalDate active_dual = lastDt.plusDays(Long.parseLong(String.valueOf(x.get("active_dual"))));
                x.put("growth_dt", active_dual.format(DateTimeFormatter.ofPattern(dateFormat)));

                if(active_dual.isAfter(LocalDate.now()) && !flag) {
                    flag = true;
                    x.put("now_flag", true);
                }else {
                    x.put("now_flag", false);
                }
                String desc = "";
                String nextActivityType="";
                switch (x.get("active_type").toString()) {
                    case "促活节点":
                        desc = "用户刚发生购买不久，后续再购概率很高";
                        nextActivityType="留存节点";
                        break;
                    case "留存节点":
                        desc = "用户发生购买经过一段时间，且大部分用户会在该阶段再购";
                        break;
                    case "弱流失预警":
                        desc = "发生购买经过较长时间，后续再购概率一般";
                        break;
                    case "强流失预警":
                        desc = "用户发生购买经过很长时间，后续再购概率很小";
                        break;
                    case "沉睡预警":
                        desc = "用户发生购买经过很长时间，后续再购概率极低";
                        break;
                    default:
                        break;
                }
                x.put("desc", desc);
                x.put("nextActivityType",nextActivityType);
            }
        }
        // 用户沟通的时间点
        List<Map<String, String>> userTimeList = userTaskMapper.getUserTimes(userId);
        List<Map<String, String>> couponList = userTaskMapper.getCouponListOfProduct(userId, productId);
        result.put("userTodayStatus", userTodayStatusList);
        result.put("userTimeList", userTimeList);
        result.put("couponList", couponList);
        return result;
    }

    @Override
    public List<Map<String, Object>> getProductData(String userId) {
        return userTaskMapper.getProductData(userId);
    }

    @Override
    public List<UserBuyHistory> getUserBuyHistory(String userId,long spuId) {
        return userTaskMapper.getUserBuyHistory(userId,spuId);
    }

    @Override
    public List<SpuInfo> getSpuList(String userId) {
        return userTaskMapper.getSpuList(userId);
    }

    @Override
    public Map<String, String> getUserStatis(String userId) {
        Map<String,String> result=Maps.newHashMap();
        //获取统计数据
        UserPurchStatsVO userPurchStatsVO=userTaskMapper.getPurchStats(userId);

        if(null!=userPurchStatsVO)
        {
            result.put("kpi11", String.format("购买金额%.2f元", userPurchStatsVO.getPurchAmout()));
            result.put("kpi12",String.format("超过了%.0f%的用户", userPurchStatsVO.getPurchAmountLevel()));

            result.put("kpi13",String.format("平均每次购买花费%.0f%元" ,userPurchStatsVO.getPurchPrice()));
            result.put("kpi14",String.format("超过了%.0f%的用户" ,userPurchStatsVO.getPurchPriceLevel()));

            result.put("kpi21",String.format("在商城购买了%.0f次" ,userPurchStatsVO.getPurchTimes()));
            result.put("kpi22",String.format("超过了%.0f%的用户" ,userPurchStatsVO.getPurchTimesLevel()));

            result.put("kpi23",String.format("平均%.0f天购买一次" ,userPurchStatsVO.getPurchInterval()));
            result.put("kpi24",String.format("超过了%.0f%的用户" ,userPurchStatsVO.getPurchIntervalLevel()));

            result.put("kpi31",String.format("在商城的折扣订单比重%.2f%" ,userPurchStatsVO.getDiscountPct()));
            result.put("kpi32",String.format("超过了%.0f%的用户" ,userPurchStatsVO.getDiscountPctLevel()));

            result.put("kpi33",String.format("在商城购买的平均折扣率%.2f%" ,userPurchStatsVO.getAvgDiscount()));
            result.put("kpi34",String.format("超过了%.0f%的用户" ,userPurchStatsVO.getAvgDiscountLevel()));
        }

       return result;
    }

    @Override
    public Map<String, String> getUserStatis(String userId, long spuId, String spuName) {
        Map<String,String> result=Maps.newHashMap();
        //获取统计数据
        UserPurchSpuStatsVO userPurchSpuStatsVO=userTaskMapper.getPurchSpuStats(userId,spuId);

        if(null!=userPurchSpuStatsVO)
        {
            result.put("kpi11", String.format("在类目%s购买了价值%.0f元的商品", spuName,userPurchSpuStatsVO.getPurchAmout()));
            result.put("kpi12",String.format("超过了%.0f%的用户", userPurchSpuStatsVO.getPurchAmountLevel()));

            result.put("kpi13",String.format("类目%s消费占自己消费总额%.2f%" ,spuName,userPurchSpuStatsVO.getPurchAmoutPct()));
            result.put("kpi14",String.format("超过了%.0f%的用户" ,userPurchSpuStatsVO.getPurchAmoutPctLevel()));

            result.put("kpi21",String.format("在类目%s购买最多的商品是%s" ,spuName,userPurchSpuStatsVO.getProdName()));
            result.put("kpi22",String.format("占类目购买订单总数的%.2f%" ,userPurchSpuStatsVO.getProdNamePct()));

            result.put("kpi23",String.format("在类目%s购买最贵的商品是%s" ,spuName,userPurchSpuStatsVO.getExpensiveProdName()));
            result.put("kpi24",String.format("占类目购买订单总数的%.2f%" ,userPurchSpuStatsVO.getExpensiveProdNamePct()));

            result.put("kpi31",String.format("在类目%s的折扣订单比重%.2f%" ,spuName,userPurchSpuStatsVO.getDiscountPct()));
            result.put("kpi32",String.format("超过了%.0f%的用户" ,spuName,userPurchSpuStatsVO.getDiscountPctLevel()));

            result.put("kpi33",String.format("在类目%s的平均折扣率%.2f%" ,userPurchSpuStatsVO.getAvgDiscount()));
            result.put("kpi34",String.format("超过了%.0f%的用户" ,userPurchSpuStatsVO.getAvgDiscountLevel()));
        }

        return result;
    }

    @Override
    public String getSpuName(long spuId) {
        return userTaskMapper.getSpuName(spuId);
    }

    @Override
    public String getFirstBuyDate(String userId) {
        return userTaskMapper.getFirstBuyDate(userId);
    }

    @Override
    public String getUserValue(String userId, long spuId) {
        return userTaskMapper.getUserValue(userId,spuId);
    }

    @Override
    public String getLifeCycle(String userId, long spuId) {
        return userTaskMapper.getLifeCycle(userId,spuId);
    }
}
