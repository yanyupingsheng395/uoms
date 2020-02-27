package com.linksteady.operate.domain;

import lombok.Data;

/**
 * @author hxcao
 * @date 2020/2/26
 */
@Data
public class ActivityCovInfo {

    private String headId;

    private String covListId;

    private String covRate;

    private String expectPushNum;

    private String expectCovNum;

    private String stage;

    private String isDefault;
}
