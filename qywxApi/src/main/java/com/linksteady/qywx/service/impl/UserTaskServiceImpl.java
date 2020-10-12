package com.linksteady.qywx.service.impl;

import com.google.common.collect.Maps;
import com.linksteady.qywx.dao.UserTaskMapper;
import com.linksteady.qywx.domain.SpuInfo;
import com.linksteady.qywx.domain.UserBuyHistory;
import com.linksteady.qywx.service.UserTaskService;
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
        log.info("lastBuyDt:" + lastBuyDt);
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
    public List<UserBuyHistory> getUserBuyHistory(String userId) {
        return userTaskMapper.getUserBuyHistory(userId);
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
            result.put("kpi12","超过了5%的用户");

            result.put("kpi13","平均每次购买花费200元");
            result.put("kpi14","超过了5%的用户");

            result.put("kpi21","在商城购买了X次");
            result.put("kpi22","超过了5%的用户");

            result.put("kpi23","平均X天购买一次");
            result.put("kpi24","超过了5%的用户");

            result.put("kpi31","在商城的折扣订单比重X%");
            result.put("kpi32","超过了5%的用户");

            result.put("kpi33","在商城购买的平均折扣率X%");
            result.put("kpi34","超过了5%的用户");
        }else
        {

        }

       return result;
    }
}
