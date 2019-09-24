package com.linksteady.operate.domain;

import lombok.Data;

/**
 * @author hxcao
 * @date 2019-09-23
 */
@Data
public class MemberStrategy {

    private Long headId;

    private String produceType;

    private Long userCount;

    private Long spuCount;

    private Long productCount;

    private String smsContent;

    private Double giftValue;

    private String reduceFlag;
}
