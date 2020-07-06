package com.linksteady.qywx.service;

/**
 * @author hxcao
 * @date 2020/7/2
 */
public interface UserMappingService {

    void deleteMappingInfo(String externalUserId, String followUserId);

    Long updateMappingInfo(String externalUserId,String followUserId,String phoneNum);

}
