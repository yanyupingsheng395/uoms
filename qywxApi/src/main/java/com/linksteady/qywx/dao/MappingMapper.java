package com.linksteady.qywx.dao;

/**
 * @author huang
 * @date 2020/7/3
 */
public interface MappingMapper {

    /**
     * 擦除匹配关系
     * @return
     */
    void flushMappingInfo(String followUserId,String externalUserId);

    Long getUserIdByUnionId(String unionId);

    void updateMappingInfo(Long userId,String followUserId,String externalUserId);

}
