package com.linksteady.qywx.service.impl;

import com.linksteady.qywx.dao.SyncTaskMapper;
import com.linksteady.qywx.domain.SyncTask;
import com.linksteady.qywx.service.SyncTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 和企业微信同步数据的服务类
 */
@Service
public class SyncTaskServiceImpl implements SyncTaskService {

    @Autowired
    SyncTaskMapper syncTaskMapper;

    @Override
    @Transactional
    public void saveFollowUser(String corpId,List<String> followUserList) {
        //更新所有记录的flag='N'
        syncTaskMapper.updateFollowUserFlag();
        //同步数据，如果insert/update，则更新falg='Y'
        syncTaskMapper.saveFollowUser(corpId,followUserList);
        //删除flag='N'的记录
        syncTaskMapper.deleteFollowUser();
    }

    @Override
    public void saveSyncTask(List<SyncTask> taskList) {
        syncTaskMapper.saveSyncTask(taskList);
    }

    @Override
    public void updateSyncTask(String taskId,String status) {
        syncTaskMapper.updateSyncTask(taskId,status);
    }
}
