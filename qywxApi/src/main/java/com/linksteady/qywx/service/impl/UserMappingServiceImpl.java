package com.linksteady.qywx.service.impl;

import com.linksteady.qywx.dao.UserMappingMapper;
import com.linksteady.qywx.service.UserMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author huang
 * 用户匹配的相关服务类
 */
@Service
public class UserMappingServiceImpl implements UserMappingService {

    @Autowired
    UserMappingMapper userMappingMapper;

    protected final Object updateMappingLock = new Object();

    @Override
    public void deleteMappingInfo(String externalUserId, String followUserId) {
        userMappingMapper.flushMappingInfo(externalUserId,followUserId);
    }

    @Override
    public Long updateMappingInfo(String externalUserId, String followUserId, String phoneNum) {
        //判断当前手机号是否在当前库存在
        Long userId=userMappingMapper.getUserIdByPhone(phoneNum);

        //更新
        synchronized (this.updateMappingLock)
        {
            if(null!=userId)
            {
                userMappingMapper.updateMappingInfo(userId,externalUserId,followUserId);
            }else
            {
                userMappingMapper.flushMappingInfo(externalUserId, followUserId);
            }

        }

        return userId;
    }
}
