package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.QywxContactWay;

import java.util.List;

/**
 * @author huang
 * @date 2020/7/17
 * 渠道活码的服务类
 */
public interface QywxContactWayService {

    void saveContactWay(QywxContactWay qywxContactWay, String userName) throws Exception;

    void updateContractWay(QywxContactWay qywxContactWay)  throws Exception;

    int getContactWayCount(String qstate);

    List<QywxContactWay> getContactWayList(int limit, int offset, String qstate);

    int getContactWayValidUrlCount();

    List<QywxContactWay> getContactWayValidUrlList(int limit, int offset);

    QywxContactWay getContactWayById(Long contactWayId);

    String getContactWayByConfigId(String configId) throws Exception;

    void updateShortUrl(Long contactWayId, String shortUrl, String updateBy);

    void deleteContactWay(String configId) throws Exception;

    QywxContactWay getQrcodeByConfigId(String configId);
}
