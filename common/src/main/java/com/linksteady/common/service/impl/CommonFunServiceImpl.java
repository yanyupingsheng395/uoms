package com.linksteady.common.service.impl;

import com.linksteady.common.bo.UserBo;
import com.linksteady.common.dao.CommonFunMapper;
import com.linksteady.common.domain.SysInfoBo;
import com.linksteady.common.service.CommonFunService;
import com.linksteady.common.util.MD5Utils;
import org.apache.catalina.security.SecurityUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hxcao
 * @date 2019-06-19
 */
@Service
public class CommonFunServiceImpl implements CommonFunService {

    @Autowired
    private CommonFunMapper commonFunMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(long userId, String password) {
        String newPass = MD5Utils.encrypt(((UserBo)SecurityUtils.getSubject().getPrincipal()).getUsername(), password);
        this.commonFunMapper.updatePassword(userId, newPass);
    }

    @Override
    public SysInfoBo getSysInfoByCode(String code) {
        return this.commonFunMapper.getSysInfoByCode(code);
    }

    @Override
    public SysInfoBo getSysInfoById(Long sysId) {
        return this.commonFunMapper.getSysInfoById(sysId);
    }

    @Override
    public boolean checkPassword(Long userId, String newPass) {
        return commonFunMapper.checkPassword(userId, newPass)>0;
    }
}
