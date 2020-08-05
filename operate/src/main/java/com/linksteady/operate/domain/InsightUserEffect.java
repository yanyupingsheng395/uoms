package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2020/8/1
 */
@Data
public class InsightUserEffect {

    private Long userId;
    private Long pushRn;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date pushDate;
    private String pushDateStr;
    private String growthPotential;
    private String isGrowth;
    private Long growthRn;
    private Long growthPushCnt;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date growthDt;
    private String growthDtStr;
    private String growthV;
}
