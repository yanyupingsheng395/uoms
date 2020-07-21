package com.linksteady.operate.dao;

import com.linksteady.operate.domain.QywxContactWay;

import java.util.List;

/**
 * @author huangkun
 * @date 2020-07-18
 */
public interface QywxContactWayMapper {

    void deleteContactWay(String configId);

    void updateShortUrl(Long contactWayId, String shortUrl,String updateBy);

    QywxContactWay getContactWayById(Long contactWayId);

    List<QywxContactWay> getAllContactWayList();

    List<QywxContactWay> getContactWayList(int limit,int offset,String qstate, String qremark);

    int getContactWayCount(String qstate, String qremark);

    void updateContactWayFullInfo(Long contactWayId,String configId,String qrCode,String shortUrl,String updateBy);

    void updateContactWayQrCode(Long contactWayId,String qrCode,String updateBy);

    void updateContractWay(QywxContactWay qywxContactWay);

    void saveContactWay(QywxContactWay qywxContactWay);

    String getCurrentDomain();

    String getQrcodeByConfigId(String configId);
}
