package com.linksteady.operate.vo;

import lombok.Data;

import java.util.List;

@Data
public class SendCouponResultVO implements java.io.Serializable{

    /**
     * 发送批次号
     */
    private Long sendRecordId;

    /**
     * 发送列表
     */
    private List<SendCouponVO> sendCouponVOList;

    public SendCouponResultVO(Long sendRecordId, List<SendCouponVO> sendCouponVOList) {
        this.sendRecordId = sendRecordId;
        this.sendCouponVOList = sendCouponVOList;
    }
}
