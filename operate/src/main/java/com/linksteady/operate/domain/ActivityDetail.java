package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2019-08-13
 */
@Data
public class ActivityDetail {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 达成目标的一般时间
     */
    private Date activDt;
}
