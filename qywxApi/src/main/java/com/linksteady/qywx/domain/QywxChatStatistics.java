package com.linksteady.qywx.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * 群人数统计
 */
@Data
public class QywxChatStatistics {
    /**
     * 日期
     */
    private long dayWid;
    /**
     * 客户群ID
     */
    private String chatId;
    /**
     * 群总人数
     */
    private long groupNumber;
    /**
     * 今日新增人数
     */
    private long addNumber;
    /**
     * 退群人数
     */
    private long outNumber;
}
