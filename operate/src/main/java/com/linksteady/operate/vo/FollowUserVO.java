package com.linksteady.operate.vo;

import lombok.Data;

/**
 * 企业微信成员
 */
@Data
public class FollowUserVO {

    /**
     * 对应企业微信成员ID
     */
    private String followUserId;

    /**
     * 对应企业微信成员名称
     */
    private String followUserName;
}
