package com.linksteady.qywx.service;

import com.linksteady.common.domain.QywxMessage;

import javax.servlet.http.HttpServletRequest;

public interface MediaService {

    /**
     * 获取欢迎语小程序卡片封面mediaId
     */
    String getMpMediaId(String identityType, Long identityId);

}
