package com.linksteady.operate.domain;

import lombok.Data;

import java.util.List;

@Data
public class QywxManualError {
    /**
     * 问题描述
     */
    private String errorDesc;
    private String errorFlag;
    private List<String> errorFollow;
    private List<String> errorExternal;


}
