package com.linksteady.operate.domain;

import com.linksteady.common.config.ExportConfig;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hxcao
 * @date 2019-08-13
 */
@Data
public class ActivityDetail implements Serializable {
    private String activityDetailId;
    private String headId;
    @ExportConfig(value = "商品ID")
    private String epbProductId;
    @ExportConfig(value = "商品名称")
    private String epbProductName;
    @ExportConfig(value = "用户ID")
    private String userId;

    private String groupId;
    private String groupName;

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

    private String userPhone;
    private String userOpenid;
    private String tarOrderPrice;
    private String tarProductNum;
    private String pushCallbackCode;
    private Date pushDate;
    private String pushDateStr;
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
    @ExportConfig(value = "推送内容")
    private String smsContent;
    private String smsTemplateContent;

    private String planType;

}
