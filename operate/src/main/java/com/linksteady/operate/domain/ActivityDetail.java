package com.linksteady.operate.domain;

import com.linksteady.common.config.ExportConfig;
import com.linksteady.operate.config.ActivityDetailExportConvert;
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
    @ExportConfig(value = "用户ID")
    private String userId;
    @ExportConfig(value = "活跃度")
    private String pathActive;
    @ExportConfig(value = "用户价值")
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
    private String smsTemplateContent;
    private String userPhone;
    private String userOpenid;
    private String tarOrderPrice;
    private String tarProductNum;
    private String pushCallbackCode;
    private Date pushDate;
    private String pushDateStr;
    private String pushOrderPeriod;
    @ExportConfig(value = "用户与商品关系")
    private String groupId;
    @ExportConfig(value = "商品ID")
    private String epbProductId;
    @ExportConfig(value = "商品名称")
    private String epbProductName;
    private String planDt;
    @ExportConfig(value = "成长节点与活动期")
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
}
