package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class QywxActivityConvertDetail implements Serializable {
    /**
     * 活动运营头ID
     */
    private Long activityHeaderId;
    /**
     * 活动运营明细ID
     */
    private Long qywxDetailId;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 活跃度
     */
    private String pathActive;
    /**
     * 用户价值
     */
    private String userValue;
    /**
     * 是否转化（0：否；1：是）
     */
    private String isConversion;
    /**
     * 推送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date execDate;
    /**
     * 转化时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date conversionDate;
    /**
     * 推送时段
     */
    private String pushPeriod;
    /**
     * 转化时段
     */
    private String conversionPeriod;
    /**
     * 推荐商品ID
     */
    private Long recProdId;
    /**
     * 推荐商品名称
     */
    private String recProdName;
    /**
     * 推荐SPU名称
     */
    private String recSpuName;
    /**
     * 推荐系列名称
     */
    private String recSeriesName;
    /**
     * 转化金额
     */
    private Long covAmount;
    /**
     * 产品是否一致（Y：一致；N不一致）
     */
    private String prodConsistent;
    /**
     * SPU是否一致（Y：一致；N不一致）
     */
    private String spuConsistent;
    /**
     * 系列是否一致（Y：一致；N不一致）
     */
    private String seriesConsistent;
    /**
     * 转化次数
     */
    private Long covNum;
    /**
     * 推荐SPU金额
     */
    private Long covSpuAmount;
    /**
     * 推荐系列转化金额
     */
    private Long covSeriesAmount;
    /**
     * 推荐SPU+系列转化金额
     */
    private Long covMultiAmount;
    /**
     * 转化间隔
     */
    private Long covInterval;
    /**
     * SPU+系列是否一致（Y：一致；N不一致）
     */
    private String multiConsistent;
    /**
     * 基于SPU收入贡献的原始值
     */
    private Long spuContribute;
    /**
     * 基于SPU收入贡献归一化值
     */
    private Long spuContributeNormal;
    /**
     * 基于用户收入贡献原始值
     */
    private Long userContribute;
    /**
     * 收入分段贡献结果
     */
    private String contributeLevel;
    /**
     * 价格水平原始值
     */
    private Long priceLevel;
    /**
     * 折扣力度原始值
     */
    private Long spuDiscount;
    /**
     * 价格敏感度归一化值
     */
    private Long spuPriceSenseNormal;
    /**
     * 价格敏感度分段结果
     */
    private String priceSenseLevel;
    /**
     * 订单的原始价值
     */
    private Long orginOrderfee;
    /**
     * 成长速度原始值
     */
    private Long orginGrowthspeed;
    /**
     * SPU极限购买次数
     */
    private Long limitTimes;
    /**
     * 价值潜力的归一化值
     */
    private Long valuePotentialNormal;
    /**
     * 价值潜力分段结果
     */
    private String valuePotentialLevel;
    /**
     * 高度活跃节点
     */
    private Long highActiveDual;
    /**
     * 中度活跃1
     */
    private Long moderateActive1Dual;
    /**
     * 中度活跃2
     */
    private Long moderateActive2Dual;
    /**
     * 流失预警节点
     */
    private Long lossWarningDual;
    /**
     * 用户次间隔的连带率
     */
    private Long actualJoint;
    /**
     * SPU平均连带率
     */
    private Long joinRate;
    /**
     * 写入时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date insertDt;
    /**
     * 数据来源
     */
    private Long groupId;
    /**
     * 推荐的商品是否为主推商品 0主推 1参活 2正常
     */
    private Long isMain;
    /**
     * 活动阶段，formal：正式，preheat:预热
     */
    private String activityStage;
    /**
     * 活动计划推送日期
     */
    private Long planDt;
    /**
     * 购买商品类型  0包含主推 1预售  2正常
     */
    private Long bProductType;
    /**
     * 推荐商品平台商品ID
     */
    private String epbProductId;
    /**
     * 推荐商品名称
     */
    private String epbProductName;
    /**
     * 计划ID
     */
    private Long planId;
    /**
     * 对应企业微信客户ID
     */
    private String qywxContractId;
    /**
     * 对应企业微信成员ID
     */
    private String followUserId;
    /**
     * 企业微信消息推送ID
     */
    private Long pushId;
    /**
     * 企业微信推送返回的消息ID
     */
    private String msgId;
    /**
     * 执行状态 -999默认表示尚未更新 0-未发送 1-已发送 2-因客户不是好友导致发送失败 3-因客户已经收到其他群发消息导致发送失败
     */
    private Long execStatus;
    /**
     * 触达状况 P计划中  S成功   F失败  R已推送,待回馈状态  C被防骚扰拦截  默认为X未推送 (其它状态由推送列表表更新过来)
     */
    private String pushStatus;

    private String qywxContactName;

    private String followUserName;
}
