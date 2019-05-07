package com.linksteady.operate.domain;

import lombok.Data;

/**
 * 诊断 加法 返回数据的接收对象
 * @author
 */
@Data
public class DiagAddDataCollector {

    public String dimValue;

    public String periodName;

    public Double value;


}
