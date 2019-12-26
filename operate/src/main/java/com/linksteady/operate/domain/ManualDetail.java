package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2019/12/25
 */
@Data
public class ManualDetail {

    private long detailId;
    private long headId;
    private String phoneNum;
    private String pushStatus;
    private Date pushDate;
}