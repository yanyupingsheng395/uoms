package com.linksteady.operate.domain;

import lombok.Data;

import javax.persistence.Transient;
import java.util.Date;

/**
 * @author hxcao
 * @date 2019-10-24
 */
@Data
public class QywxDailyHeader {

    private Long headId;

    private long totalNum;

    private Date taskDate;

    @Transient
    private String taskDateStr;

    private String status;

    private long successNum;

    /**
     * 转化人数
     */
    private long convertNum;

    /**
     * 转化金额
     */
    private double convertAmount;

    /**
     * 转化率
     */
    private double convertRate;


    /**
     * 推荐SPU转化人数
     */
    private long convertSpuNum;

    /**
     * 推荐SPU转化金额
     */
    private double convertSpuAmount;

    /**
     * 推荐SPU转化率
     */
    private double convertSpuRate;

    /**
     * 最后计算日期
     */
    private Date statDate;

    /**
     * 验证标记
     */
    private String validateLabel;

    /**
     * 效果统计天数
     */
    private long effectDays;

    private int version;

    private Date insertDt;

    private Date updateDt;

    private int qywxMessageCount;

    private int staffCnt;

    /**
     * 校验结果描述
     */
    private String checkDesc;

    private double pushSuccessRate;

    private int executeStaffCnt;

    private String executeRate;

    /**
     * 是否已进行优惠券发放的标记
     */
    private String couponSendFlag;
}
