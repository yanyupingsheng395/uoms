package com.linksteady.qywx.service.impl;

import com.linksteady.common.service.impl.BaseService;
import com.linksteady.qywx.dao.UserMapper;
import com.linksteady.qywx.domain.*;
import com.linksteady.qywx.service.QywxService;
import com.linksteady.qywx.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl extends BaseService<QywxUser> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    QywxService qywxService;

    @Override
    public QywxUser findUser(String userId, String corpId) {
        Example example = new Example(QywxUser.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andCondition("USER_ID=", userId);
        criteria.andCondition("CORP_ID=",corpId);
        List<QywxUser> list = this.selectByExample(example);

        //填充公司名称
        QywxUser qywxUser=list.isEmpty() ? null : list.get(0);
        if(null!=qywxUser)
        {
            qywxUser.setCorpName(getCorpName(corpId));
        }
        return qywxUser;
    }

    @Override
    public void saveUser(UserInfoThirdParty userInfoThirdParty, UserDetailThirdParty userDetailThirdParty) {
        QywxUser qywxUser=new QywxUser();
        qywxUser.setUserId(userInfoThirdParty.getUserId());
        qywxUser.setCorpId(qywxService.getCorpId());
        qywxUser.setIsAdmin("N");
        qywxUser.setInsertDt(new Date());
        qywxUser.setUpdateDt(new Date());

        if(null!=userDetailThirdParty)
        {
            qywxUser.setUserName(userDetailThirdParty.getName());
            qywxUser.setAvatar(userDetailThirdParty.getAvatar());
            qywxUser.setQrCode(userDetailThirdParty.getQrCode());
            qywxUser.setGender(userDetailThirdParty.getGender());
        }
        this.save(qywxUser);
    }

    @Override
    public void saveUser(UserInfoSso userInfoSso) {
        QywxUser qywxUser=new QywxUser();
        qywxUser.setUserId(userInfoSso.getUserId());
        qywxUser.setCorpId(qywxService.getCorpId());
        qywxUser.setAvatar(userInfoSso.getAvatar());
        qywxUser.setIsAdmin(userInfoSso.getUserType());
        this.save(qywxUser);
    }

    @Override
    public List<QywxUser> getAdminList(String corpId) {
        Example example = new Example(QywxUser.class);
        Example.Criteria criteria=example.createCriteria();

        criteria.andCondition("CORP_ID=",corpId);
        criteria.andCondition("is_admin=", "Y");
        List<QywxUser> list = this.selectByExample(example);
        return list;
    }

    @Override
    @Transactional
    public void saveAdminInfo(String corpId,List<ApplicationAdmin> applicationAdminList) {
        //对管理员进行保存
        userMapper.saveAdminInfo(applicationAdminList);
        //更新登录用户表
        userMapper.flushQywxUserAdminInfo(corpId);
        userMapper.updateQywxUserAdminInfo(corpId);
    }

    /**
     * 根据公司ID获取公司名称
     */
    private String getCorpName(String corpId)
    {
        String corpName=userMapper.getCorpName(corpId);
        if(StringUtils.isEmpty(corpName))
        {
            corpName="";
        }
        return corpName;
    }

}
