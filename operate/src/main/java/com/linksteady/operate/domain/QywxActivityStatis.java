package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class QywxActivityStatis   implements Serializable {
    /**
     * 活动运营head_id
     */
    private Long activityHeadId;
    /**
     * 推送转化率
     */
    private Double convertRate;
    /**
     * 触达日期
     */
    private Long touchDate;
    /**
     * 推送转化人数
     */
    private Long convertNum;
    /**
     * 推荐购买SPU转化人数
     */
    private Long convertSpuNum;
    /**
     * 推荐购买SPU转化率
     */
    private Double convertSpuRate;
    /**
     * 写入日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date insertDt;
    /**
     * 转化日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date conversionDate;
    /**
     * 转化日期转成字符串
     */
    private String conversionDateStr;


}
