package com.linksteady.operate.domain;

import lombok.Data;

/**
 * @author hxcao
 * @date 2019-08-02
 */
@Data
public class DailyPush {

    private Long userId;

    private String smsContent;

    private Long headId;

    private String status;
}
