package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author huang
 * @date 2019-10-25
 */
@Data
public class ExecSteps {

    private String keyName;
    private String isValid;
    private String stepType;
    private String stepName;
    private String sqlContent;
    private String beanName;
    private String methodName;
    private int orderNo;
    private String sqlType;
    private String comments;
}