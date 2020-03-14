package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ExecSteps execSteps = (ExecSteps) o;
        return (stepType+sqlContent+beanName+methodName).equals(execSteps.getStepType()+execSteps.getSqlContent()+execSteps.getBeanName()+execSteps.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stepType, sqlContent, beanName, methodName);
    }
}