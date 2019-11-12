package com.linksteady.operate.vo;

import lombok.Data;

@Data
public class ActivityGroupVO {

    private String groupName;

    private String inGrowthPath;

    private String activeLevel;

    /**
     * 活动推送内容
     */
    private String content;

    /**
     * 非活动推送内容
     */
    private String contentNormal;
}
