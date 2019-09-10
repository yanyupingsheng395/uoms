package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.util.DataStatisticsUtils;
import com.linksteady.common.util.DateUtil;
import com.linksteady.operate.dao.ActivityHeadMapper;
import com.linksteady.operate.dao.ActivityProductMapper;
import com.linksteady.operate.dao.ActivityUserMapper;
import com.linksteady.operate.domain.ActivityProduct;
import com.linksteady.operate.domain.ActivityUser;
import com.linksteady.operate.service.ActivityProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2019-09-07
 */
@Slf4j
@Service
public class ActivityProductServiceImpl implements ActivityProductService {

    @Autowired
    private ActivityProductMapper activityProductMapper;

    @Autowired
    private ActivityUserMapper activityUserMapper;

    @Autowired
    private ActivityHeadMapper activityHeadMapper;

    @Override
    public int getCount(String headId) {
        return activityProductMapper.getCount(headId);
    }

    @Override
    public List<ActivityProduct> getActivityProductListPage(int start, int end, String headId) {
        return activityProductMapper.getActivityProductListPage(start, end, headId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveActivityProductBySql(String startDate, String endDate, String headId) {
        Long dayPeriod = DateUtil.countDay(startDate, endDate);
        activityProductMapper.deleteByHeadId(headId);
        activityProductMapper.insertProductList(startDate, endDate, headId, dayPeriod);

        Long userCnt = activityUserMapper.getCoverUserCnt(startDate, endDate, dayPeriod);
        activityHeadMapper.updateCoverUserCnt(headId, userCnt);
    }

    /**
     * 优惠值：建议优惠面额求均值
     * 建议销售额：商品单价 - 优惠值
     * 15天之内最低价：订单表中商品ID对应的最低价
     * 30天之内最低价：订单表中商品ID对应的最低价
     *
     * @param startDate
     * @param endDate
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveActivityProduct(String startDate, String endDate, String headId) {
        // 促销价是100
        final double ACT_PROD_PRICE = 100D;
        // 原价分布的中位数
        final double PRODUCT_PRICE_MID = 370D;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        List<ActivityProduct> productList = Lists.newLinkedList();
        Long dayPeriod = DateUtil.countDay(startDate, endDate);

        /**
         * DATA_TYPE(数据类型):
         *  1=>每日运营数据: 可以通过开始结束日期筛选日期
         *  2=>阶段目标数据: 魔法值（90） + 活动影响时间开始结束的时间长度 >= REMAIN_TIME的值
         */
        LinkedList<ActivityUser> activityUserList = activityUserMapper.getActivityUser(startDate, endDate, dayPeriod);
        // 根据推荐商品ID分组
        Map<Long, List<ActivityUser>> dataMap = activityUserList.stream().collect(Collectors.groupingBy(ActivityUser::getRecProdId));
        for (Map.Entry<Long, List<ActivityUser>> x : dataMap.entrySet()) {
            ActivityProduct product = new ActivityProduct();
            product.setHeadId(Long.valueOf(headId));
            product.setProductId(String.valueOf(x.getKey()));


            product.setProductPrice(Optional.ofNullable(x.getValue().get(0).getPrice()).orElse(null));

            product.setUserCount((long) x.getValue().size());
            Double preferValue = x.getValue().stream().map(p -> p.getReferDeno()).collect(Collectors.averagingDouble(v -> {
                if (null == v || v.isEmpty()) {
                    return 0D;
                } else {
                    return Double.valueOf(v);
                }
            }));
            product.setPreferValue(decimalFormat.format(preferValue));
            product.setProductActPrice(Double.valueOf(decimalFormat.format(product.getProductPrice() - Double.valueOf(product.getPreferValue()))));
            product.setMinPrice15(Double.valueOf(x.getValue().get(0).getMinPrice15()));
            product.setMinPrice30(Double.valueOf(x.getValue().get(0).getMinPrice30()));
            productList.add(product);
        }

        // 获取所有商品的原价
        productList.stream().forEach(x -> {
            if (x.getProductActPrice() <= ACT_PROD_PRICE) {
                x.setPreferType(PreferType.PROMOTE.code);
            } else {
                if (x.getProductPrice() >= PRODUCT_PRICE_MID) {
                    x.setPreferType(PreferType.REDUCE.code);
                } else {
                    x.setPreferType(PreferType.DISCOUNT.code);
                }
            }
        });
        activityProductMapper.saveDataList(productList);

        // 更新头表预计覆盖人数
        Long userCnt = activityUserMapper.getCoverUserCnt(startDate, endDate, dayPeriod);
        activityHeadMapper.updateCoverUserCnt(headId, userCnt);
    }

    @Override
    public Map<String, Object> getCouponBoxData(String productId, String startDate, String endDate) {
        DecimalFormat df = new DecimalFormat("0.00");
        Map<String, Object> result = Maps.newHashMap();
        List<ActivityUser> activityUserList = activityUserMapper.getActivityUserByDateAndProductId(productId, startDate, endDate);
        DoubleSummaryStatistics dss = activityUserList.stream().map(x -> x.getReferDeno()).collect(Collectors.summarizingDouble(v -> Double.valueOf(v)));

        List<Double> couponList = activityUserList.stream().map(x -> Double.valueOf(x.getReferDeno())).collect(Collectors.toList());
        double[] d = new double[couponList.size()];
        for (int i = 0; i < couponList.size(); i++) {
            d[i] = couponList.get(i);
        }
        d = DataStatisticsUtils.getQuartiles(d);
        result.put("q1", df.format(d[0]));
        result.put("q2", df.format(d[1]));
        result.put("q3", df.format(d[2]));
        result.put("max", dss.getMax());
        result.put("min", dss.getMin());
        result.put("avg", df.format(dss.getAverage()));

        return result;
    }


    enum PreferType {
        DISCOUNT("discount", "打折", " 所有在售商品，原价分布的中位数370元以下的商品"),
        REDUCE("reduce", "满减", "所有在售商品，原价分布的中位数370元以上的商品"),
        PROMOTE("promote", "促销价", "打完折或者减完钱之后，优惠价在100以内的商品");
        private String code;
        private String name;
        private String desc;
        PreferType(String code, String name, String desc) {
            this.code = code;
            this.name = name;
            this.desc = desc;
        }
    }
}
