package com.linksteady.qywx.service;

import java.util.List;
import java.util.Map;

/**
 * @author huang
 * @date 2020/7/2
 */
public interface ParamService {

    /**
     * 获取企业微信部署的IP地址
     * @return
     */
    String getQywxDomainAddress();

    /**
     * 获取企业微信应用的域名
     */
    String getQywxDomainUrl();

    /**
     * 获取当前应用绑定的企业微信的corpId
     */
    String getQywxCorpId();

    /**
     * 获取关联小程序的appid
     * @return
     */
    String getMpAppId();

    /**
     * 获取当前用户的corpId
     * @return
     */
    String getCorpId();

    /**
     * 获取当前应用的secret
     */
    String getSecret();

}
