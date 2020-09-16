package com.linksteady.qywx.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpuInfo implements Serializable {

    private Long spuId;

    private String spuName;
}
