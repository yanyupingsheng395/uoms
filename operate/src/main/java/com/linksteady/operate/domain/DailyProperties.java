package com.linksteady.operate.domain;

import lombok.Data;

/**
 * 每日运营配置信息
 * @author
 */
@Data
public class DailyProperties {

    /**
     * 是否开启避免重复推送
     */
   private String repeatPush="N";

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
 private String alertPhoneNum;


}
