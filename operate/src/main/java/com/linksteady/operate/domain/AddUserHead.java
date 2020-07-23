package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2020/7/16
 */
@Data
public class AddUserHead {

    private int id;

    private String sendType;

    private Integer applyUserCnt;

    private String applyPassCnt;

    private Double applyPassRate;

    private Date taskStartDt;

    private String taskStatus;

    private String addUserMethod;
}
