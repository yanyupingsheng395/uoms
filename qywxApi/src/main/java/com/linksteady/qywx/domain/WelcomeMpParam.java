package com.linksteady.qywx.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WelcomeMpParam {

    private String wcMediaId;

    private LocalDateTime wcMediaExpireDate;

    /**
     * 欢迎语小程序封面内容
     */
    private byte[] wcMediaContent;
}
