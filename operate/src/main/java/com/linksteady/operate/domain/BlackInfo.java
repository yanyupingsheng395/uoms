package com.linksteady.operate.domain;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author hxcao
 * @date 2020/3/31
 */
@Data
public class BlackInfo {

    private String userPhone;

    private String expireDate;

    private String smsContent;

    private String insertBy;

    private String insertType;

    private LocalDate insertDate;

}
