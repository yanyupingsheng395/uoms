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

    /**
     * 进行一次匹配操作
     */
    void mappingAll();

    /**
     * 对已匹配上的用户进行清除操作
     */
    void unMappingAll();

}
