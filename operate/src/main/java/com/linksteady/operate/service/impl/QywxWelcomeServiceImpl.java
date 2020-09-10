package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.QywxWelcomeMapper;
import com.linksteady.operate.domain.QywxWelcome;
import com.linksteady.operate.service.QywxWelcomeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(rollbackFor = Exception.class)
    public Integer saveData(QywxWelcome qywxWelcome) {
        String policyType = qywxWelcome.getPolicyType();
        if (StringUtils.isNotEmpty(policyType) && policyType.equalsIgnoreCase("A")) {
            qywxWelcome.setPolicyType(qywxWelcome.getPolicyTypeTmp());
        }
        qywxWelcomeMapper.saveData(qywxWelcome);
        System.out.println(qywxWelcome);
        return qywxWelcome.getId();
    }

    @Override
    public int getDataCount() {
        return qywxWelcomeMapper.getDataCount();
    }

    @Override
    public List<QywxWelcome> getDataList(Integer limit, Integer offset) {
        return qywxWelcomeMapper.getDataList(limit, offset);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String id) {
        qywxWelcomeMapper.deleteById(id);
    }

    @Override
    public QywxWelcome getDataById(String id) {
        List<QywxWelcome> welcomeList = qywxWelcomeMapper.getDataById(id);
        return welcomeList.size() > 0 ? welcomeList.get(0) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateData(QywxWelcome qywxWelcome) {
        String policyType = qywxWelcome.getPolicyType();
        if (StringUtils.isNotEmpty(policyType) && policyType.equalsIgnoreCase("A")) {
            qywxWelcome.setPolicyType(qywxWelcome.getPolicyTypeTmp());
        }
        qywxWelcomeMapper.updateData(qywxWelcome);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(String id, String status) {
        if(StringUtils.isNotEmpty(status)) {
            if(status.equalsIgnoreCase("start")) {
                qywxWelcomeMapper.updateStartStatus(id, status);
            } else if(status.equalsIgnoreCase("stop")) {
                qywxWelcomeMapper.updateStopStatus(id, status);
            }
        }
    }
}
