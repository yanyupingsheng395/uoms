package com.linksteady.operate.domain;

import lombok.Data;

/**
 * 每日运营配置信息
 * @author
 */
@Data
public class DailyProperties {

    /**
     * 是否开启避免重复推送 默认全部开启
     */
   //private String repeatPush="N";

    /**
     * 避免重复推送的天数
     */
    private int repeatPushDays=7;

    /**
     * 是否开启推送
     */
    private String pushFlag="Y";

    /**
     * 效果统计的天数
     */
    private int statsDays=20;

    /**
     * 推送方式
     */
    private String pushType="SMS";

    /**
     * 开启预警
     */
    private String openAlert="Y";

 /**
  * 预警手机号码 (暂时不启用)
  */
 private String alertPhone;

 /**
  * 推送方式 IMME:IMME立即推送 AI:AI智能推送
  */
 private String pushMethod;

 /**
  * 优惠券领用方式 MANUAL 表示需要手动领取 AUTO表示会自动打入用户账号
  */
 private String couponMthod;

 /**
  * 券是否需要处理成短链接
  */
 private String couponUrlToShort;

 /**
  * 短信是否包含产品明细页链接
  */
 private String includeProdUrl;

 /**
  * 产品明细页是否需要处理成短链接
  */
 private String prodUrlToShort;

 /**
  * 短信内容的长度限制
  */
 private int smsLengthLimit=66;

 /**
  * 当前修改用户
  */
 private String currentUser;
}
