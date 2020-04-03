package com.linksteady.operate.domain;

import com.linksteady.common.config.ExportConfig;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author hxcao
 * @date 2019-09-07
 */
@Data
public class ActivityProduct {

    private Long id;

    private Long headId;

    @ExportConfig(value = "商品ID")
    private String productId;

    @ExportConfig(value = "名称")
    private String productName;

    @ExportConfig(value = "非活动日常单价（元/件）")
    private double formalPrice;

    @ExportConfig(value = "活动动机制", convert = "s:1=活动价,2=满件打折,3=满元减钱,4=特价")
    private String groupId;

    @ExportConfig(value = "活动通知体现最低单价（元/件）")
    private double notifyMinPrice;

    @ExportConfig(value = "活动期间体现最低单价（元/件）")
    private double minPrice;

    @ExportConfig(value = "商品短链")
    private String productUrl;

    private String productAttr;

    private String skuCode;

    private String checkFlag;

    private String checkComments;

    private String alikeProdId;

    /**
     * 判断当前数据是否合法
     * @return
     */
    public boolean productValid() {
        return StringUtils.isNotEmpty(this.getProductId()) &&
                StringUtils.isNotEmpty(this.getGroupId()) &&
                0D != this.getMinPrice() &&
                0D != this.getFormalPrice() &&
                0D != this.getNotifyMinPrice();
    }
}
