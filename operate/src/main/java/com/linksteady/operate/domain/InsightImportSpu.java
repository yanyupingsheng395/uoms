package com.linksteady.operate.domain;


import lombok.Data;

@Data
public class InsightImportSpu {

  private String spuId;
  private String spuName;
  private String purchOrder;
  private String contributeRate;
  private String nextPurchProbal;
  private String sameSpuProbal;
  private String otherSpuProbal;
  private String computeDt;
}
