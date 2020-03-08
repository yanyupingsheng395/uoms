package com.linksteady.operate.domain;

import lombok.Data;

/**
 * @author hxcao
 * @date 2019-09-07
 */
@Data
public class ActivityProduct {

    private Long id;

    private Long headId;

    private String productId;

    private String productName;

    private double minPrice;

    private double formalPrice;

    private String productUrl;

    private String productAttr;

    private String skuCode;

    private double notifyMinPrice;

    private String checkFlag;

    private String checkComments;

    private String alikeProdId;

    private String groupId;
}
