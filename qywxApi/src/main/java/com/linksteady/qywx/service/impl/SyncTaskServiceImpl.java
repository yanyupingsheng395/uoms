package com.linksteady.qywx.service.impl;

import com.linksteady.qywx.dao.SyncTaskMapper;
import com.linksteady.qywx.domain.ExternalContact;
import com.linksteady.qywx.domain.SyncTask;
import com.linksteady.qywx.service.SyncTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
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

    @Override
    public void updateDeptDisabled() {
        syncTaskMapper.updateDeptDisabled();
    }

    @Override
    public void saveDept(long id, long parentId, String name, int orderNo) {
        syncTaskMapper.saveDept(id,parentId,name,orderNo);
    }

    @Override
    public void saveChangeFlag(String changeCode) {
        if("party".equals(changeCode))
        {
            syncTaskMapper.savePartyChangeFlag("Y");
        }else if("auth_scope".equals(changeCode))
        {
            syncTaskMapper.saveAuthCodeChangeFlag("Y");
        }else if("follow_user".equals(changeCode))
        {
            syncTaskMapper.saveFollowUserChangeFlag("Y");
        }

    }

    @Override
    public void saveChangeFlagToN(String changeCode) {
        if("party".equals(changeCode))
        {
            syncTaskMapper.savePartyChangeFlag("N");
        }else if("auth_scope".equals(changeCode))
        {
            syncTaskMapper.saveAuthCodeChangeFlag("N");
        }else if("follow_user".equals(changeCode))
        {
            syncTaskMapper.saveFollowUserChangeFlag("N");
        }
    }

    @Override
    public void saveExternalContactList(List<ExternalContact> externalContactList) {
        externalContactList.stream().forEach(i->
        {
            i.setAddDate(timeStampToDate(i.getCreatetime()));
        });
        syncTaskMapper.saveExternalContactList(externalContactList);
    }


    @Override
    public void delExternalContact(String externalUserId, String followUserId, String corpId) {
        syncTaskMapper.deleteExternalContact(followUserId,externalUserId);
    }

    @Override
    public void saveExternalContact(ExternalContact externalContact) {
        externalContact.setAddDate(timeStampToDate(externalContact.getCreatetime()));
        syncTaskMapper.saveExternalContact(externalContact);
    }


    /**
     * 10位字符串 时间戳转日期
     * @param timeStamp
     * @return
     */
    private Date timeStampToDate(String timeStamp)
    {
        if(StringUtils.isEmpty(timeStamp))
        {
            return null;
        }else
        {
            return new Date(Long.parseLong(timeStamp)*1000);
        }

    }
}
