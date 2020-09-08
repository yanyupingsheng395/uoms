package com.linksteady.operate.domain;

import lombok.Data;

/**
 * @author hxcao
 * @date 2020/9/8
 */
@Data
public class QywxWelcomeProduct {
    private int productId;
    private String productName;
    private Double price;
    private String productType;
    private String productUrl;
    private String checkFlag;
    private String checkContent;
}
