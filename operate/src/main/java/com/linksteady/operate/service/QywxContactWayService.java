package com.linksteady.operate.service;

import com.alibaba.fastjson.JSONObject;
import com.linksteady.operate.domain.QywxContactWay;

import java.util.List;

/**
 * @author huang
 * @date 2020/7/17
 * 渠道活码的服务类
 */
public interface QywxContactWayService {

    void saveContactWay(QywxContactWay qywxContactWay) throws Exception;

    void updateContractWay(QywxContactWay qywxContactWay)  throws Exception;

    int getContactWayCount(String qstate,String remark);

    List<QywxContactWay> getContactWayList();

    List<QywxContactWay> getContactWayList(int limit,int offset,String qstate,String qremark);

    QywxContactWay getContactWayById(Long contactWayId);

    JSONObject getContactWayByConfigId(String configId);

    void updateShortUrl(Long contactWayId,String shortUrl,String updateBy);

    void deleteContactWay(String configId) throws Exception;

    String getQrcodeByConfigId(String configId);

}
