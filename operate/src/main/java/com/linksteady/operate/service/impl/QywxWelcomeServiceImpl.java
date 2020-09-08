package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.QywxWelcomeMapper;
import com.linksteady.operate.domain.QywxWelcome;
import com.linksteady.operate.service.QywxWelcomeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/9/3
 */
@Service
public class QywxWelcomeServiceImpl implements QywxWelcomeService {

    @Autowired
    private QywxWelcomeMapper qywxWelcomeMapper;

    @Override
    public void saveData(QywxWelcome qywxWelcome) {
        String policyType = qywxWelcome.getPolicyType();
        if(StringUtils.isNotEmpty(policyType) && policyType.equalsIgnoreCase("A")) {
            qywxWelcome.setPolicyType(qywxWelcome.getPolicyTypeTmp());
        }
        qywxWelcomeMapper.saveData(qywxWelcome);
    }

    @Override
    public int getDataCount() {
        return qywxWelcomeMapper.getDataCount();
    }

    @Override
    public List<QywxWelcome> getDataList(Integer limit, Integer offset) {
        return qywxWelcomeMapper.getDataList(limit, offset);
    }
}
