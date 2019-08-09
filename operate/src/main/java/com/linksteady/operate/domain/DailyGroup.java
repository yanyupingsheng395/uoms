package com.linksteady.operate.domain;

import lombok.Data;

/**
 * @author hxcao
 * @date 2019-07-31
 */
@Data
public class DailyGroup {

    /**
     * ID
     */
    private Long groupId;

    /**
     * 头表ID
     */
    private Long headId;

    /**
     * 组名称
     */
    private String groupName;

    /**
     * 组描述
     */
    private String groupDesc;

    /**
     * 生命周期
     */
    private String lifeCycle;

    /**
     * 价值
     */
    private String userValue;

    /**
     * 活跃度
     */
    private String pathActive;

    /**
     * 目标分类
     */
    private String tarType;

    /**
     * 是否多路径
     */
    private String pathMulti;

    /**
     * 用户数量
     */
    private Long userCount;

    /**
     * 是否选中
     */
    private String isCheck;

    /**
     * 紧迫度
     */
    private String urgencyLevel;

    /**
     * 短信模板
     */
    private String smsCode;

    /**
     * 模板内容
     */
    private String smsContent;

    private String isNew;
}
