package com.linksteady.qywx.dao;


import com.linksteady.common.config.MyMapper;
import com.linksteady.qywx.domain.ApplicationAdmin;
import com.linksteady.qywx.domain.QywxUser;

import java.util.List;

public interface UserMapper extends MyMapper<QywxUser> {

    String getCorpName(String corpId);

    void saveAdminInfo(List<ApplicationAdmin> applicationAdminList);

    void flushQywxUserAdminInfo(String corpId);

    void updateQywxUserAdminInfo(String corpId);
}