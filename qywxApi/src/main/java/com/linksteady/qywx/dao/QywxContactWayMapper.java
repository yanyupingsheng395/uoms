package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.QywxContactWay;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author huangkun
 * @date 2020-07-18
 */
public interface QywxContactWayMapper {

    void deleteContactWay(String configId);

    void updateShortUrl(Long contactWayId, String shortUrl, String updateBy);

    QywxContactWay getContactWayById(Long contactWayId);

    List<QywxContactWay> getContactWayList(int limit, int offset, String qstate);

    int getContactWayCount(@Param("qstate") String qstate);

    List<QywxContactWay> getContactWayValidUrlList(int limit, int offset);

    int getContactWayValidUrlCount();

    void updateContactWayFullInfo(Long contactWayId, String configId, String qrCode, String shortUrl, String updateBy);

    void updateContactWayQrCode(Long contactWayId, String qrCode, String updateBy);

    void updateContractWay(QywxContactWay qywxContactWay);

    void saveContactWay(QywxContactWay qywxContactWay);

    String getCurrentDomain();

    QywxContactWay getQrcodeByConfigId(String configId);

    int getRefrenceCount(String configId);
}
