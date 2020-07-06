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

}
