package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.OrderSeries;

import java.util.List;

/**
 * @author huang
 * @date 2020/7/3
 */
public interface ApiMapper {

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

}
