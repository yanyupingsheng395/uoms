package com.linksteady.operate.service;

import com.linksteady.operate.domain.BlackInfo;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/3/31
 */
public interface BlackService {

    List<BlackInfo> getDataList(String phone, int start, int end);

    int getCount(String phone);

    void deleteByPhone(String phone);

    void insertData(BlackInfo blackInfo);
}
