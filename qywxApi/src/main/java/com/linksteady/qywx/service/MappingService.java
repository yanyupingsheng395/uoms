package com.linksteady.qywx.service;

/**
 * @author hxcao
 * @date 2020/7/2
 */
public interface MappingService {

    void deleteMappingInfo( String followUserId,String externalUserId);

    Long updateMappingInfo(String followUserId,String externalUserId,String unionId);

    /**
     * 进行全量的用户匹配
     */
    void mappingAll();

    /**
     * 对已匹配上的用户进行清除操作
     */
    void unMappingAll();

}
