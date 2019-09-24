package com.linksteady.operate.domain;

import lombok.Data;

/**
 * @author hxcao
 * @date 2019-09-23
 */
@Data
public class MemberDetail {

    private Long detailId;

    private Long headId;

    private Long userId;

    /**
     * 目标spu
     */
    private String targetSpu;

    /**
     * spu购买次序
     */
    private String spuBuyTime;

    /**
     * 目标商品
     */
    private String targetProduct;

    /**
     * 目标商品件数（件）
     */
    private Long targetProductNum;

    /**
     * 距离成长节点（天）
     */
    private Long growthDay;

    /**
     * 活跃度
     */
    private String pathActive;

    /**
     * 价值
     */
    private String userValue;

    /**
     * 品牌广度
     * single:单一SPU
     * multi：多种SPU
     */
    private String brandDeep;

    /**
     * 预计连带率
     */
    private String joinRate;
}
