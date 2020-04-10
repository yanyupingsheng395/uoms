package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.linksteady.operate.dao.BlackMapper;
import com.linksteady.operate.domain.BlackInfo;
import com.linksteady.operate.service.BlackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hxcao
 * @date 2020/3/31
 */
@Service
public class BlackServiceImpl implements BlackService {

    @Autowired
    private BlackMapper blackMapper;

    @Override
    public List<BlackInfo> getDataList(String phone, int limit, int offset) {
        return blackMapper.getDataList(phone, limit,offset);
    }

    @Override
    public int getCount(String phone) {
        return blackMapper.getCount(phone);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByPhone(String phone) {
        List<String> phoneList = new ArrayList<>(((JSONArray)JSONObject.parse(phone)).toJavaList(String.class));
        if(phoneList.size() > 0) {
            blackMapper.deleteByPhone(phoneList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertData(BlackInfo blackInfo) {
        blackMapper.insertData(blackInfo);
    }

    @Override
    public boolean checkPhone(String phone) {
        return blackMapper.checkPhone(phone) == 0;
    }
}
