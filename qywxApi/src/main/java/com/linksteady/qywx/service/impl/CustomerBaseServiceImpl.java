package com.linksteady.qywx.service.impl;

import com.linksteady.qywx.dao.CustomerBaseMapper;
import com.linksteady.qywx.domain.FollowUser;
import com.linksteady.qywx.domain.QywxContractDetail;
import com.linksteady.qywx.domain.QywxContractList;
import com.linksteady.qywx.service.CustomerBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerBaseServiceImpl implements CustomerBaseService {

    @Autowired
    private CustomerBaseMapper customerBaseMapper;

    @Override
    public int getCount() {
        return customerBaseMapper.getCount();
    }

    @Override
    public List<QywxContractList> getDataList(int limit, int offset) {
        return customerBaseMapper.getDataList(limit, offset);
    }

    @Override
    public int getCustomerListCount(long groupId) {
        return customerBaseMapper.getCustomerListCount(groupId);
    }

    @Override
    public List<QywxContractDetail> getCustomerList(int limit, int offset, long groupId) {
        return customerBaseMapper.getCustomerList(limit, offset,groupId);
    }

    @Override
    public List<FollowUser> getFollowUser() {
        return customerBaseMapper.getFollowUser();
    }

}
