package com.linksteady.operate.vo;

import com.linksteady.operate.domain.DiagCondition;
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
