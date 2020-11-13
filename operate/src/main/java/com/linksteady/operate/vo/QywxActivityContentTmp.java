package com.linksteady.operate.vo;

import lombok.Data;

@Data
public class QywxActivityContentTmp {
    /**
     * 行表ID
     */
    private Long qywxDetailId;
    /**
     * 计划ID
     */
    private Long planId;
    /**
     * 消息内容
     */
    private String textContent;
    /**
     * 短信计费条数
     */
    private String qywxMsgSign;
    /**
     * 小程序标题
     */
    private String mpTitle;
    /**
     * 小程序路径
     */
    private String mpUrl;

}
