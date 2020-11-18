package com.linksteady.qywx.service.impl;

import com.linksteady.qywx.dao.MappingMapper;
import com.linksteady.qywx.service.MappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author huang
 * 用户匹配的相关服务类
 */
@Service
public class MappingServiceImpl implements MappingService {

    @Autowired
    MappingMapper mappingMapper;

    protected final Object updateMappingLock = new Object();

    @Override
    public void deleteMappingInfo(String followUserId,String externalUserId) {
        mappingMapper.flushMappingInfo(followUserId,externalUserId);
    }

    @Override
    public Long updateMappingInfo(String followUserId,String externalUserId,String unionId) {
        //判断当前手机号是否在当前库存在
        Long userId=mappingMapper.getUserIdByUnionId(unionId);

        //更新
        synchronized (this.updateMappingLock)
        {
            if(null!=userId)
            {
                mappingMapper.updateMappingInfo(userId,followUserId,externalUserId);
            }else
            {
                mappingMapper.flushMappingInfo(followUserId,externalUserId);
            }
        }
        return userId;
    }

    @Override
    public void mappingAll() {
        //todo
    }
}
