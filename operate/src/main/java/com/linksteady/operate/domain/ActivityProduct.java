package com.linksteady.operate.domain;

import com.linksteady.common.config.ExportConfig;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;

/**
 * @author hxcao
 * @date 2019-09-07
 */
@Data
public class ActivityProduct implements Cloneable {

    private Long id;

    private Long headId;

    @ExportConfig(value = "商品ID")
    private String productId;
    @ExportConfig(value = "名称")
    private String productName;
    @ExportConfig(value = "店铺活动动机制", convert = "s:9=满件打折,10=满元减钱,11=特价秒杀,12=预售付尾立减,13=无店铺活动")
    private String groupId;
    @ExportConfig(value = "活动通知体现最低单价（元/件）")
    private String notifyMinPrice;
    @ExportConfig(value = "活动通知体现利益点")
    private String notifyProfit;
    @ExportConfig(value = "活动期间体现最低单价（元/件）")
    private String duringMinPrice;
    @ExportConfig(value = "活动期间体现利益点")
    private String duringProfit;
    private double minPrice;
    private String productUrl;
    private String productAttr;
    private String skuCode;
    private String checkFlag;
    private String checkComments;
    private String alikeProdId;
    private Double discountSize;
    private Double discountAmount;
    private Double activityPrice;
    private Double activityProfit;
    private String activityType;
    private String activityStage;
    private double formalPrice;
    // 上传出现的错误信息
    private String errorInfo;
    // 是否是单品券：1是，0否
    private String spCouponFlag;
    // 单品券门槛
    private Double spCouponThreshold;
    // 单品券面额
    private Double spCouponDenom;

    /**
     * 判断当前数据是否合法
     *
     * @return
     */
    public boolean productValid() {
        if ("1".equalsIgnoreCase(this.getSpCouponFlag())) {
            return StringUtils.isNotEmpty(this.getProductId()) && StringUtils.isNotEmpty(this.getGroupId()) &&
                    ("9".equalsIgnoreCase(this.getGroupId()) && 0D != this.getDiscountSize() ||
                            "13".equalsIgnoreCase(this.getGroupId()) && 0D != this.getDiscountAmount() ||
                            "14".equalsIgnoreCase(this.getGroupId()) && 0D != this.getDiscountAmount())
                    && (0D != this.getSpCouponThreshold()) && (0D != this.getSpCouponDenom());
        } else {
            return StringUtils.isNotEmpty(this.getProductId()) &&
                    StringUtils.isNotEmpty(this.getGroupId()) &&
                    (("9".equalsIgnoreCase(this.getGroupId()) && 0D != this.getDiscountSize())
                            || ("13".equalsIgnoreCase(this.getGroupId()) && 0D != this.getDiscountAmount())
                            || ("14".equalsIgnoreCase(this.getGroupId()) && 0D != this.getDiscountAmount()));
        }
    }

    @Override
    public ActivityProduct clone() {
        ActivityProduct activityProduct = null;
        try {
            activityProduct = (ActivityProduct) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return activityProduct;
    }
}
