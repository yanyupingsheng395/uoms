package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.OrderSeries;

import java.util.List;

/**
 * @author huang
 * @date 2020/7/3
 */
public interface UserMappingMapper {

    /**
     * 擦除匹配关系
     * @return
     */
    List<OrderSeries> flushMappingInfo(String externalUserId,String followUserId);

    Long getUserIdByPhone(String mobile);

    void updateMappingInfo(Long userId,String externalUserId,String followUserId);

}
