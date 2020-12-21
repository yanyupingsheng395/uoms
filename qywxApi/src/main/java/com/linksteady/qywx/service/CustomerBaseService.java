package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.FollowUser;
import com.linksteady.qywx.domain.QywxContractDetail;
import com.linksteady.qywx.domain.QywxContractList;

import java.util.List;

public interface CustomerBaseService {

    int getCount();

    /**
     * 获取客户群列表
     * @param limit
     * @param offset
     * @return
     */
    List<QywxContractList> getDataList(int limit, int offset);

    /**
     * 获取客户群人数
     * @param
     * @return
     */
    int getCustomerListCount(String chatId);

    List<QywxContractDetail> getCustomerList(int limit, int offset, String chatId);

    List<FollowUser> getFollowUser();
}
