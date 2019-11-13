package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2019-08-13
 */
@Data
public class ActivityDetail {
    private String activityDetailId;
    private String headId;
    private String userId;
    private String pathActive;
    private String userValue;
    private String spuName;
    private String recPiecePrice;
    private String joinRate;
    private String orderPeriod;
    private String referDeno;
    private String recProdId;
    private String recProdName;
    private String completePurch;
    private String recRetentionId;
    private String recRetentionName;
    private String recUpId;
    private String recUpName;
    private String recCrossId;
    private String recCrossName;
    private String recType;
    private String isPush;
    private String isConversion;
    private String pushStatus;
    private String smsContent;
    private String smsTemplateContent;
    private String userPhone;
    private String userOpenid;
    private String tarOrderPrice;
    private String tarProductNum;
    private String pushCallbackCode;
    private Date pushDate;
    private String pushDateStr;
    private String pushOrderPeriod;
    private String groupId;
    private String epbProductId;
    private String epbProductName;
    private String planDt;
    private String inGrowthPath;
    private String spuContribute;
    private String spuContributeNormal;
    private String userContribute;
    private String contributeLevel;
    private String priceLevel;
    private String spuDiscount;
    private String spuPriceSenseNormal;
    private String priceSenseLevel;
    private String orginOrderfee;
    private String orginGrowthspeed;
    private String limitTimes;
    private String valuePotentialNormal;
    private String valuePotentialLevel;
    private String highActiveDual;
    private String moderateActive1Dual;
    private String moderateActive2Dual;
    private String lossWarningDual;
    private String actualJoint;
    private Date insertDt;
    private String activityStage;
}
