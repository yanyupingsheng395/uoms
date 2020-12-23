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
     * 用户总数
     */
    private Integer totalCnt;

    /**
     * 添加用户数
     */
    private Integer addCnt;

    /**
     * 购买用户数
     */
    private Integer purchCnt;

    /**
     * 购买金额
     */
    private double totalAmount;


    /**
     * 接收消息条数
     */
    private Integer receiveMsg;

    /**
     * 推送消息条数
     */
    private Integer pushMsg;

    /**
     * 消息任务对应推送用户数
     */
    private Integer receiveUserCnt;

    /**
     * 推送用户数
     */
    private Integer pushUserCnt;

    /**
     * 推送购买用户数
     */
    private Integer pushCovCnt;

    /**
     * 累计推送购买金额
     */
    private double pushTotalAmount;


}
