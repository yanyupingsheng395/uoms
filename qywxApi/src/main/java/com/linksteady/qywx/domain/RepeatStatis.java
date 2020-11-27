package com.linksteady.qywx.domain;

import lombok.Data;

@Data
public class RepeatStatis {

    private String externalUserId;
    private String name;
    private String followUser;
    private int cnt;
}
