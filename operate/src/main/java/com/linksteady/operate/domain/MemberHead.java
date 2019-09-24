package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2019-09-23
 */
@Data
public class MemberHead {

    private Long headId;

    private Long userCount;

    private Date memberDate;

    private String memberDateStr;

    private Long convertUserCount;

    private Double convertRate;

    private Double convertAmount;

    private String status;
}
