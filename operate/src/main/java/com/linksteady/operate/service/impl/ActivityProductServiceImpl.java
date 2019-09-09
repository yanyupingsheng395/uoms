package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.util.DataStatisticsUtils;
import com.linksteady.operate.dao.ActivityProductMapper;
import com.linksteady.operate.dao.ActivityUserMapper;
import com.linksteady.operate.domain.ActivityProduct;
import com.linksteady.operate.domain.ActivityUser;
import com.linksteady.operate.service.ActivityProductService;
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
@Service
public class ActivityProductServiceImpl implements ActivityProductService {

    @Autowired
    private ActivityProductMapper activityProductMapper;

    @Autowired
    private ActivityUserMapper activityUserMapper;

    @Override
    public int getCount(String headId) {
        return activityProductMapper.getCount(headId);
    }

    @Override
    public List<ActivityProduct> getActivityProductListPage(int start, int end, String headId) {
        return activityProductMapper.getActivityProductListPage(start, end, headId);
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
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        List<ActivityProduct> productList = Lists.newLinkedList();

        List<ActivityUser> activityUserList = activityUserMapper.getActivityUserByDate(startDate, endDate);
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
            product.setProductActPrice(product.getProductPrice() - Double.valueOf(product.getPreferValue()));
            productList.add(product);
        }

        // 获取所有商品的原价
        productList.stream().forEach(x -> {
            if (x.getProductActPrice() <= 100) {
                x.setPreferType(PreferType.PROMOTE.code);
            } else {
                if (x.getProductPrice() >= 370D) {
                    x.setPreferType(PreferType.REDUECE.code);
                } else {
                    x.setPreferType(PreferType.DISCOUNT.code);
                }
            }
        });
        activityProductMapper.saveDataList(productList);
    }


    private Map<String, Object> getLowestProductPrice(String productId) {
        return activityProductMapper.getLowestProductPrice(productId);
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
        /**
         * 打折
         */
        DISCOUNT("discount", "打折", " 所有在售商品，原价分布的中位数370元以下的商品"),

        /**
         * 满减
         */
        REDUECE("reduce", "满减", "所有在售商品，原价分布的中位数370元以上的商品"),

        /**
         * 促销价
         */
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
