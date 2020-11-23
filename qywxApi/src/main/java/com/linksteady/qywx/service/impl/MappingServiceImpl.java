package com.linksteady.qywx.service.impl;

import com.linksteady.qywx.dao.MappingMapper;
import com.linksteady.qywx.service.MappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author huang
 * 用户匹配的相关服务类
 */
@Service
@Slf4j
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
        //判断unionid是否已经存在
        Long userId=mappingMapper.getUserIdByUnionId(unionId);

        //更新
        synchronized (this.updateMappingLock)
        {
            //如果已经存在，则更新 否则清除匹配信息
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
        log.info("匹配全部外部联系人开始");
        mappingMapper.mappingAll();
    }

    @Override
    public void unMappingAll() {
        log.info("清除过期的匹配信息");
        mappingMapper.unMappingAll();
    }
}
