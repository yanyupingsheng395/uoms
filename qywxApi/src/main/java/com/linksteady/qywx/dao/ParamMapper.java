package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.QywxParam;

/**
 * @author huang
 * @date 2020/7/3
 */
public interface ParamMapper {

    QywxParam getQywxParam();

    void updateCorpInfo(String corpId,String applicationSecret);

    void updateContact(String eventToken, String eventAesKey);

    void updateEnableWelcome(String enableWelcome);

    void updateMpAppId(String mpAppId);
}
