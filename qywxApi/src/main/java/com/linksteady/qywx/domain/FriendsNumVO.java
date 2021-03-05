package com.linksteady.qywx.domain;

import lombok.Data;

@Data
public class FriendsNumVO {

    /**
     * 添加好友数量
     */
    private long friendsNum;
    /**
     * 未添加好友数量
     */
    private long unFriendsNum;
}
