package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.FollowUser;
import com.linksteady.qywx.domain.QywxContractDetail;
import com.linksteady.qywx.domain.QywxContractList;

import java.util.List;

public interface CustomerBaseMapper {
    int getCount();

    List<QywxContractList> getDataList(int limit, int offset);

    int getCustomerListCount(String chatId);

    List<QywxContractDetail> getCustomerList(int limit, int offset, String chatId);

    List<FollowUser> getFollowUser();

}
