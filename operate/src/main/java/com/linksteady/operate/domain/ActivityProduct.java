package com.linksteady.operate.domain;

import lombok.Data;

/**
 * @author hxcao
 * @date 2019-09-07
 */
@Data
public class ActivityProduct {

    private Long headId;

    private String productId;

    private Long userCount;

    private String preferType;

    private String preferValue;

    private Double minPrice15;

    private Double minPrice30;

    private Double productPrice;

    private Double productActPrice;
}
