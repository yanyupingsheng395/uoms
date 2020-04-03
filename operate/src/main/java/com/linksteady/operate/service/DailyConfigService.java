package com.linksteady.operate.service;

import java.util.Map;

public interface DailyConfigService {

    Map<String, Object> usergroupdesc(String userValue, String pathActive, String lifecycle);

    boolean validUserGroup();

    void deleteSmsGroup(String groupId);
}
