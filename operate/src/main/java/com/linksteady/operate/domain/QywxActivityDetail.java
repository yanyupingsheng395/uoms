package com.linksteady.operate.domain;

import com.linksteady.common.config.ExportConfig;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class QywxActivityDetail implements Serializable {
    /**
     * 活动明细ID
     */
    private Long qywxDetailId;
    private Long headId;
    /**
     * 用户id
     */
    @ExportConfig(value = "用户ID")
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
     * 目标SPU
     */
    private String spuName;
    /**
     * 推荐价单价
     */
    private String recPiecePrice;
    /**
     * 连带率
     */
    private String joinRate;
    /**
     * 建议触达时段
     */
    private String orderPeriod;
    /**
     * 建议补贴面额
     */
    private String referDeno;
    /**
     * 目标商品ID
     */
    private String recProdId;
    /**
     * 目标商品名称
     */
    private String recProdName;
    /**
     * 目标SPU购买次序
     */
    private String completePurch;
    /**
     * 留存推荐商品ID
     */
    private String recRetentionId;
    /**
     * 留存推荐商品名称
     */
    private String recRetentionName;
    /**
     * 向上推荐商品id
     */
    private String recUpId;
    /**
     * 向上推荐商品名称
     */
    private String recUpName;
    /**
     * 交叉推荐商品id
     */
    private String recCrossId;
    /**
     * 交叉推荐商品名称
     */
    private String recCrossName;
    /**
     * 推荐类型：1、留存推荐；2、向上推荐；3、交叉推荐
     */
    private String recType;
    /**
     * 是否触达
     */
    private String isPush;
    /**
     * 是否转化
     */
    private String isConversion;
    /**
     * 触达状况 P计划中  S成功   F失败  R已推送,待回馈状态  C被防骚扰拦截  默认为X未推送 (其它状态由推送列表表更新过来)
     */
    private String pushStatus;
    /**
     * 消息内容
     */
    @ExportConfig(value = "推送内容")
    private String smsContent;
    /**
     * 消息模板内容
     */
    private String smsTemplateContent;
    /**
     * 用户手机号
     */
    private String userPhone;
    /**
     * 用户OPENID
     */
    private String userOpenid;
    /**
     * 目标订单价（元/单）
     */
    private String tarOrderPrice;
    /**
     * 目标商品件数
     */
    private String tarProductNum;
    /**
     * 推送回调返回编码
     */
    private String pushCallbackCode;
    /**
     * 实际推送时间
     */
    private Date pushDate;
    /**
     * 分组类型 1-8
     */
    private String groupId;
    /**
     * 推荐EPB_PRODUCT_ID
     */
    @ExportConfig(value = "商品ID")
    private String epbProductId;
    /**
     * 推荐short_name
     */
    @ExportConfig(value = "商品名称")
    private String epbProductName;
    /**
     * 活动计划推送日期
     */
    private String planDt;
    /**
     * 是否在成长节点 0:否 1:是
     */
    private String inGrowthPath;
    /**
     * 基于spu收入贡献的原始值
     */
    private String spuContribute;
    /**
     * 基于spu收入贡献归一化值
     */
    private String spuContributeNormal;
    /**
     * 收入贡献分段结果
     */
    private String contributeLevel;
    /**
     * 价格敏感度归一化值
     */
    private String priceSenseNormal;
    /**
     * 价格敏感度分段结果
     */
    private String priceSenseLevel;
    /**
     * 订单价的原始值
     */
    private String orginOrderfee;
    /**
     * 成长速度的原始值
     */
    private String orginGrowthspeed;
    /**
     * 价值潜力的归一化值
     */
    private String valuePotentialNormal;
    /**
     * 价值潜力的分段结果
     */
    private String valuePotentialLevel;
    /**
     * 高度活跃节点
     */
    private String highActiveDual;
    /**
     * 中度活跃1
     */
    private String moderateActive1Dual;
    /**
     * 中度活跃2
     */
    private String moderateActive2Dual;
    /**
     * 流失预警节点
     */
    private String lossWarningDual;
    /**
     * 用户次间隔的连带率
     */
    private String actualJoint;
    /**
     * 插入时间
     */
    private Date insertDt;
    /**
     * 是否可培养
     */
    private String educateValue;
    /**
     * 价格敏感度原始值
     */
    private String priceSense;
    /**
     * 价值潜力原始值
     */
    private String valuePotential;
    /**
     * 弱流失节点
     */
    private String weakLossDual;
    /**
     * 强流失节点
     */
    private String strongLossDual;
    /**
     * 是否需要弱流失
     */
    private String needWeakRec;
    /**
     * 计划推送时间
     */
    private String pushSchedulingDate;
    /**
     * 计划ID
     */
    private Long planId;
    /**
     * 商品参加活动的利益点
     */
    private double activityProfit;
    /**
     * 活动文案中填充的最低价
     */
    private double activityPrice;
    /**
     * 短信计费条数
     */
    private String smsBillingCount;
    /**
     * 小程序标题
     */
    private String mpTitle;
    /**
     * 小程序路径
     */
    private String mpUrl;
    /**
     * 企业微信消息唯一签名
     */
    private String qywxMsgSign;
    /**
     * 企业微信消息推送ID
     */
    private Integer pushId;
    /**
     * 对应企业微信客户ID
     */
    private String qywxContractId;
    /**
     * 对应企业微信成员ID
     */
    private String followUserId;

    /**
     * 对应企业微信成员名称
     */
    private String followUserName;

    /**
     * 外部客户名称
     */
    private String qywxContactName;


    private String mpMediaId;

}
