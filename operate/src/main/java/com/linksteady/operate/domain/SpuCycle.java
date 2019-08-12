package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "UO_LC_SPU_TRAGET")
public class SpuCycle {
    /**
     * 用户ID
     */
    @Column(name = "USER_ID")
    private String userId;

    /**
     * SPU/品类
     */
    @Column(name = "SPU_NAME")
    private String spuName;

    /**
     * 复购
     */
    @Column(name = "RE_PURCH")
    private String rePurch;

    /**
     * 平均购买间隔
     */
    @Column(name = "PURCH_INTERVAL")
    private String purchInterval;

    /**
     * 平均购买品类种数
     */
    @Column(name = "NUM_PURCH_TYPE")
    private String numPurchType;

    /**
     * 平均连带率
     */
    @Column(name = "JOINT_RATE")
    private String jointRate;

    /**
     * 周期阶段
     */
    @Column(name = "LIFECYCLE_TYPE")
    private String lifecycleType;

    /**
     * 累计购买次数
     */
    @Column(name = "ACC_PURCH_TIMES")
    private String accPurchTimes;

    /**
     * 平均件单价
     */
    @Column(name = "AVG_PIECE_PRICE")
    private String avgPiecePrice;

    /**
     * 品类ID
     */
    @Column(name = "SPU_ID")
    private String spuId;

    /**
     * 完成最早时间
     */
    @JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
    @Column(name = "HIGH_ACTIVE_DUAL")
    private Date highActiveDual;

    /**
     * 完成集中时间
     */
    @JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
    @Column(name = "MODERATE_ACTIVE_DUAL")
    private Date moderateActiveDual;

    /**
     * 完成最晚时间
     */
    @JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
    @Column(name = "LOSS_WARNING_DUAL")
    private Date lossWarningDual;

    /**
     * 剩余购买次数
     */
    @Column(name = "SURPLUS_TIMES")
    private Long surplusTimes;
}