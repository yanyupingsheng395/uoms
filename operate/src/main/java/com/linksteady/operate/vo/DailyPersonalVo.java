package com.linksteady.operate.vo;

import lombok.Data;

/**
 * 每日运营个体效果查询参数
 * @author hxcao
 * @date 2019-10-28
 */
@Data
public class DailyPersonalVo {

    /**
     * 是否转化
     */
    private String isConvert;

    /**
     * 推荐SPU是否转化
     */
    private String spuIsConvert;

    /**
     * 转化SPU
     */
    private String convertSpu;

    /**
     * 用户价值
     */
    private String userValue;

    /**
     * 用户活跃度
     */
    private String pathActive;
}
