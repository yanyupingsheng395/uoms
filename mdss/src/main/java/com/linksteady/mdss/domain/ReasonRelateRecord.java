package com.linksteady.mdss.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReasonRelateRecord implements Serializable {

    String reasonId;

    String fcode;

    String fname;

    String rfcode;

    String rfname;

    String forderNo;

    String rforderNo;

    String relateValue;





}
