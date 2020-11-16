package com.linksteady.qywx.dao;

/**
 * @author huang
 * @date 2020/7/3
 */
public interface ParamMapper {

    String getMpAppId();

    /**
     * 获取当前公司的corpId
     * @return
     */
    String getCorpId();


    String getSecret();

    void updateCorpInfo(String corpId,String applicationSecret);

}
