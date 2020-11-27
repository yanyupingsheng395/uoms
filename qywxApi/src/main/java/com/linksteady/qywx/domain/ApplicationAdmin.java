package com.linksteady.qywx.domain;

import lombok.Data;

@Data
public class ApplicationAdmin {

    private String corpId;

    private String userId;

    private int authType;

    public ApplicationAdmin(String corpId, String userId, int authType) {
        this.corpId = corpId;
        this.userId = userId;
        this.authType = authType;
    }
}
