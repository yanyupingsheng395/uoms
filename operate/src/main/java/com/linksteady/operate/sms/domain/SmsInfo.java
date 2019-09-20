package com.linksteady.operate.sms.domain;

import lombok.Data;

/**
 * @author hxcao
 * @date 2019-09-12
 */
@Data
public class SmsInfo {

    /**
     * id
     */
    private String id;

    /**
     * user id
     */
    private String userId;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 内容
     */
    private String content;

    /**
     * 触达时间
     */
    private String touchTime;

    /**
     * 单条短信的状态
     */
    private String status;
}
