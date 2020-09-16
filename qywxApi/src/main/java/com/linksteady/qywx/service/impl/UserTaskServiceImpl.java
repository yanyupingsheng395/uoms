package com.linksteady.qywx.service.impl;

import com.google.common.collect.Maps;
import com.linksteady.qywx.dao.UserTaskMapper;
import com.linksteady.qywx.service.UserTaskService;
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
                switch (x.get("active_type").toString()) {
                    case "促活节点":
                        desc = "用户刚发生购买不久，后续再购概率很高";
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

    public static void main(String[] args) {
        System.out.println(LocalDate.now().getDayOfWeek().getValue());
    }

    @Override
    public List<Map<String, Object>> getProductData(String userId) {
        return userTaskMapper.getProductData(userId);
    }

    @Override
    public List<Map<String, Object>> getUserBuyHistory(String userId) {
        return userTaskMapper.getUserBuyHistory(userId);
    }
}
