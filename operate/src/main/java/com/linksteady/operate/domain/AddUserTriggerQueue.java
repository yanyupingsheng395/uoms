package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

@Data
public class AddUserTriggerQueue {
    private Long orderId;
    private Long userId;
    private Date orderDt;
    private String mobile;
    private String sourceName;
    private String productName;

    private String sourceId;
    private String regionId;
}
