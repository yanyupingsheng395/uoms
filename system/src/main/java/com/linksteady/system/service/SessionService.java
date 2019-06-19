package com.linksteady.system.service;

import java.util.List;

import com.linksteady.common.domain.UserOnline;

public interface SessionService {

	List<UserOnline> list();

	boolean forceLogout(String sessionId);
}
