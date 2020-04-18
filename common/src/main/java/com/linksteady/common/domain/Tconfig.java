package com.linksteady.common.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Tconfig implements Serializable {

    private String name;
    private String value;
    private String comments;
    private int orderNum;
    private String typeCode1;
    private String typeCode2;
}
