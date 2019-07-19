package com.linksteady.mdss.vo;

import com.linksteady.mdss.domain.DiagCondition;
import lombok.Data;

import java.util.List;

/**
 * Created by hxcao on 2019-05-30
 */
@Data
public class NodeDataVO {
    private String id;

    private String parentid;

    private Boolean isroot;

    private String topic;

    private String kpiCode;

    private String kpiName;

    private String kpiLevelId;

    private String alarmFlag;

    private List<DiagCondition> conditions;
}
