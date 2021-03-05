package com.linksteady.qywx.domain;

import lombok.Data;

@Data
public class QywxChatStatisticsVO {
    //群ID
    private String chatId;
    //日期
    private long joinDay;
    //某一天人数
    private long cnt;
    //总人数
    private long grandTotalCnt;
}
