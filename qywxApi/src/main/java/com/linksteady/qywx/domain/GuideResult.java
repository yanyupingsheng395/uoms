package com.linksteady.qywx.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hxcao
 * @date 2020/9/17
 */
@Data
public class GuideResult implements Serializable {

    /**
     * 导购id
     */
    private String followUserId;

    /**
     * 日期  YYYYMMDD格式
     */
    private Long dayWid;

    /**
     * 接收消息条数
     */
    private int receiveMsg;

    /**
     * 推送消息条数
     */
    private int pushMsg;

    /**
     * 消息任务对应推送用户数
     */
    private int receiveUserCnt;

    /**
     * 推送用户数
     */
    private int pushUserCnt;

    /**
     * 推送购买用户数
     */
    private int pushCovCnt;

    /**
     * 累计推送购买金额
     */
    private double pushTotalAmount;


}
