package com.linksteady.system.service;

public interface QywxLoginService {

    String getCorpId();

    String getAgentId();

    String getOauthFileContent();

    /**
     * 判断校验文件是否存在
     * @param authFileName
     */
    int queryAuthFile(String authFileName);
}
