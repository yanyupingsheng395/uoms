package com.linksteady.operate.domain;

import com.linksteady.common.config.ExportConfig;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 企业微信每日运营--个体效果结果类
 *
 * @author hxcao
 * @date 2019-10-28
 */
@Data
public class QywxDailyPersonalEffect implements Serializable {
    // 用户ID
    @ExportConfig(value = "用户ID")
    private String userId;

    /**
     * 是否转化
     * 需要设置默认值：否
      */
    @ExportConfig(value = "是否转化", convert = "s:1=是,0=否")
    private String isConvert;

    // 转化间隔(天)
    @ExportConfig(value = "转化间隔（天）")
    private String convertInterval;

    // 推送spu
    @ExportConfig(value = "推送SPU")
    private String pushSpu;

    // 转化spu
    @ExportConfig(value = "转化SPU")
    private String convertSpu;

    // 推送spu是否转化
    @ExportConfig(value = "推送SPU是否转化", convert = "s:Y=是,N=否")
    private String spuIsConvert;

    // 用户价值
    @ExportConfig(value = "用户价值", convert = "s:ULC_01=重要,ULC_02=主要,ULC_03=普通,ULC_04=长尾")
    private String userValue;

    // 用户活跃度
    @ExportConfig(value = "用户活跃度", convert = "s:UAC_01=高度活跃,UAC_02=中度活跃,UAC_03=流失预警,UAC_04=弱流失,UAC_05=强流失,UAC_06=沉睡")
    private String pathActive;

    // 推送日期
    private Date execDate;

    // 转化日期
    private String convertDate;

    private String followUserId;

    private String followUserName;

    private String qywxContractId;

    private String qywxContactName;
}
