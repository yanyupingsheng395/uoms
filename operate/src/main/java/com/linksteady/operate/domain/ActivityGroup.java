package com.linksteady.operate.domain;

import lombok.Data;

/**
 * @author hxcao
 * @date 2019-10-31
 */
@Data
public class ActivityGroup {

    private Long groupId;

    private Long headId;

    private String groupName;

    private String isGrowthPath;

    private String activeLevel;

    private Long groupUserCnt;

    private Long growthUserCnt;

    private Long activeUserCnt;
}
