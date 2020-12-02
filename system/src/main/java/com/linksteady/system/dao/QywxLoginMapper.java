package com.linksteady.system.dao;

public interface QywxLoginMapper {

    String getCorpId();

    String getAgentId();

    String getOauthFileContent();

    int queryAuthFile(String authFileName);
}
