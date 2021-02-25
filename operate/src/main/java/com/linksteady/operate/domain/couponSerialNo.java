package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class couponSerialNo implements Serializable {
    private Long couponId;
    /**
     * 流水号编号
     */
    private String serialNo;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime insertDt;
    /**
     * 是否使用 Y 已使用 N未使用
     */
    private String usedFlag;
    /**
     * 使用时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime usedDt;
    /**
     * 商城优惠券唯一标记
     */
    private String couponIdentity;
}
