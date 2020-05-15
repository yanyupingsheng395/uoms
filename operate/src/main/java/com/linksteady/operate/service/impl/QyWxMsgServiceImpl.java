package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.QywxMapper;
import com.linksteady.operate.service.QyWxMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hxcao
 * @date 2020/5/15
 */
@Service
public class QyWxMsgServiceImpl implements QyWxMsgService {

    @Autowired
    private QywxMapper qywxMapper;
}