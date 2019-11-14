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
    @ExportConfig(value = "用户与商品关系", convert = "s:1=成长用户,2=成长用户,3=成长用户,4=成长用户,5=潜在用户,6=潜在用户,7=潜在用户,8=潜在用户")
    private String groupId;
    @ExportConfig(value = "活跃度", convert = "s:UAC_01=高度活跃,UAC_02=中度活跃,UAC_03=流失预警,UAC_04=弱流失,UAC_05=强流失,UAC_06=沉睡")
    private String pathActive;
    @ExportConfig(value = "用户价值", convert = "s:ULC_01=重要,ULC_02=主要,ULC_03=普通,ULC_04=长尾")
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
